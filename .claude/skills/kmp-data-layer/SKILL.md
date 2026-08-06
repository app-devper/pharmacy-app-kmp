---
name: kmp-data-layer
description: Build or extend :core:data in the pharmacy app — the shared Ktor client and its response validator, ApiConfig URLs, @Serializable DTOs, mappers in repository/internal/, RepositoryImpls, local storage, DI. Use when adding an endpoint, fixing a mapping, or wiring a new transport.
---

# kmp-data-layer

`:core:data` is the only module that knows about HTTP, DTOs and Ktor. It
implements the repository interfaces from `:core:domain` and is bound only by
`:composeApp` — no `:features:*` may import it (audit A20).

```
[ ViewModel ]→[ UseCase ]→[ Repository interface (:core:domain) ]
                                     ▲ implements
                          [ RepositoryImpl ]→[ <X>Api ]→[ shared Ktor HttpClient ]
                                     │                        └ HttpResponseValidator → typed AppException
                                     └ repository/internal/<X>Mapper.kt : Dto → domain, Input → Dto
```

```
core/data/src/commonMain/kotlin/app/devper/pharm/data/
  network/     HttpClient.kt, ApiConfig.kt
  remote/api/  <X>Api.kt
  remote/dto/  <X>Dto.kt
  repository/  <X>RepositoryImpl.kt
  repository/internal/  <X>Mapper.kt
  storage/     TokenStorage, ParkedCartStorage, StockCountDraftStorage, OfflineSaleQueueImpl, *Dto
  internal/    DateConv.kt
  di/          DataModule.kt
```

## 1. The shared client — `network/HttpClient.kt`

```kotlin
fun <T : HttpClientEngineConfig> buildHttpClient(
    engine: HttpClientEngineFactory<T>,
    tokenStorage: TokenStorage,
    sessionExpiry: SessionExpiryProvider,
    json: Json = AppJson,
    enableLogging: Boolean = false,
    installTimeout: Boolean = true,
): HttpClient
```

Built **per platform** (each entry point passes its engine — OkHttp / Darwin /
Js), so the factory lives here but the binding lives in `<plat>PlatformModule`.

- `expectSuccess = false` **on purpose** — an explicit `HttpResponseValidator`
  does the translating, so there is one place that maps status → typed
  exception instead of two competing mechanisms. Don't "fix" it to `true`.
- `DefaultRequest` sets JSON content type and the `Authorization: Bearer`
  header from `TokenStorage`.
- Timeouts: connect 15s, request 30s, socket 30s.
- `AppJson` is `ignoreUnknownKeys = true`, `isLenient = true`,
  `encodeDefaults = false`, `explicitNulls = false`.
- The validator throws `AuthException` (and clears the token + marks the
  session expired), `ForbiddenException`, `NotFoundException`,
  `ConflictException(payload)`, else `ServerException(statusCode, body)`;
  transport failures become `NetworkException`. Full detail in
  `kmp-error-handling`.

## 2. URLs — `network/ApiConfig.kt`

```kotlin
data class ApiConfig(val apiBaseUrl: String = "https://api.devper.app") {
    fun pharmacy(path: String): String = "$apiBaseUrl/api/pharmacy/v1${path.ensureLeadingSlash()}"
    fun umUser(path: String = ""): String = "$apiBaseUrl/api/um/v1/user${path.ensureLeadingSlash()}"
    val umAuthLogin: String get() = "$apiBaseUrl/api/um/v1/auth/login"
    …
}
```

Apis never hardcode a host — they take `ApiConfig` and call `config.pharmacy("/customers")`.
Auth goes to the UM hub; everything else to the pharmacy service.

`localQaApiBaseUrl(pageHost, rawQuery)` lets the web build point at a local
mock via `?apiBaseUrl=…`, but **only** when the page host is localhost. Keep
that guard.

## 3. `<X>Api`

```kotlin
class CustomerApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {
    suspend fun list(): List<CustomerDto> = client.get(config.pharmacy("/customers")).body()

    suspend fun add(request: CustomerInputDto): CustomerDto =
        client.post(config.pharmacy("/customers")) { setBody(request) }.body()

    suspend fun update(id: String, request: CustomerInputDto) {
        client.put(config.pharmacy("/customers/$id")) { setBody(request) }
    }
}
```

No `try/catch` — the validator already threw typed. One method per endpoint,
DTOs in and out.

## 4. DTOs — `@SerialName` on everything (A24/A25)

```kotlin
@Serializable
data class CustomerDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("price_tier") val priceTier: String? = null,
    @SerialName("disease") val disease: String? = null,
)
```

- **Every** property carries an explicit `@SerialName`, even when it matches
  the Kotlin name — the audit fails otherwise, and it makes the wire contract
  readable at a glance.
- Kotlin names are always camelCase; a `val price_tier` fails A25.
- One line each, with a default so optional fields decode forgivingly.
- DTOs stay `Double` / `Int`. `Money` and `Quantity` are domain types — the
  wire doesn't know about them.
- Wire names sometimes disagree with the domain (`disease` → `allergyNote`).
  Keep the wire name in the DTO and rename in the mapper; don't rename the wire.

## 5. Mappers — `repository/internal/<X>Mapper.kt`

Mapping lives in its own file as `internal fun` extensions, **not** private at
the bottom of the impl — 18 mappers share the package and several are reused by
more than one repository.

```kotlin
internal fun CustomerDto.toDomain(): Customer = Customer(
    id = id,
    name = name,
    phone = phone?.takeIf { it.isNotBlank() },
    priceTier = priceTier?.takeIf { it.isNotBlank() } ?: "",
    allergyNote = disease?.takeIf { it.isNotBlank() },
)

internal fun CustomerInput.toDto(): CustomerInputDto = CustomerInputDto(
    name = name.trim(),
    phone = phone.trim(),
    disease = allergyNote.trim(),
    priceTier = priceTier.trim(),
)
```

Conventions: `toDomain()` inbound, `toDto()` outbound; normalise here (`trim()`,
blank → null); wrap value classes inbound (`Money(dto.sellPrice)`) and unwrap
outbound (`input.price.amount`); dates go through
`String?.parseLocalDateTimeOrNull()` / `parseLocalDateOrNull()`
(`data/internal/DateConv.kt`), which convert UTC → Bangkok when an offset
marker is present and accept both `YYYY-MM-DD` and full datetimes.

## 6. `RepositoryImpl`

```kotlin
class CustomerRepositoryImpl(private val api: CustomerApi) : CustomerRepository {
    override suspend fun list(): List<Customer> = api.list().map { it.toDomain() }
    override suspend fun add(input: CustomerInput): Customer = api.add(input.toDto()).toDomain()
    override suspend fun update(id: String, input: CustomerInput) { api.update(id, input.toDto()) }
}
```

Bare `T` returns, no `Result`, no `runCatching`, no `try/catch`. **Domain never
sees a DTO.** A repository may talk to more than one Api or to local storage,
but repositories never compose other repositories.

## 7. Local storage

`data/storage/` holds the non-HTTP side: `TokenStorage`, `ParkedCartStorage`,
`StockCountDraftStorage`, `OfflineSaleQueueImpl` (the offline sale queue), and
their `*Dto` types. Those DTOs follow the same A24/A25 rules — the audit scans
every `*Dto.kt` under `data/`.

## 8. DI — `data/di/DataModule.kt`

```kotlin
val dataModule = module {
    singleOf(::CustomerApi)
    singleOf(::CustomerRepositoryImpl) bind CustomerRepository::class
    …
}
```

One `Api` + `RepositoryImpl` pair per domain. The `HttpClient`, `Settings` and
`SecureStorage` come from the platform module. `dataModule` is included by
`:composeApp/di/AppModule.kt` only.

## 9. Testing

`:core:data:commonTest` already covers the shapes worth copying:

- **`HttpResponseValidatorTest`** — status → typed exception, using `MockEngine`.
- **`*ApiContractTest`** (Drug / Inventory / Sale) — the request path, body
  shape and decoding against a `MockEngine`.
- **`SaleDtoTest` / `AppJsonTest`** — serialization edge cases.
- **`DateConvTest`** — timezone conversion.
- **`TokenStorageTest`, `OfflineSaleQueueTest`, `StockCountDraftStorageTest`** —
  storage, using `MemorySettings` / `InMemorySecureStorage` fakes from the same
  source set.

Feature tests never touch a `RepositoryImpl` — they fake the domain interface
(`kmp-test`).

## 10. Anti-patterns

- A `@Serializable` property without `@SerialName` (A24) or with a snake_case
  Kotlin name (A25).
- `runCatching` / `try/catch` in an Api or `RepositoryImpl`.
- A DTO imported into `:core:domain` or `:features:*`.
- `:features:*` importing `app.devper.pharm.data.*` (A20).
- An Api hardcoding a host instead of taking `ApiConfig`.
- Flipping `expectSuccess` to `true` — it would double up with the validator.
- Widening `localQaApiBaseUrl` beyond localhost.
- Mapping logic inlined in the repository instead of `repository/internal/`.
