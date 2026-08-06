---
name: kmp-data-layer
description: Build or extend the data layer of a Compose Multiplatform Clean-Architecture project — Ktor HttpClient, Api class, @Serializable DTOs, RepositoryImpl that maps DTO ↔ domain, multi-tenant client switching, DI bindings. Use when adding/changing an HTTP endpoint, fixing a DTO mapping, or wiring a new transport.
---

# kmp-data-layer

The data layer lives in `:core:data`. It is the only place that:
- knows about HTTP / DTOs / Ktor
- depends on `:core:domain` (to implement repository interfaces, never to leak DTOs upward)
- is bound by `:composeApp` (no `:features:*` may touch it — audit rule A20)

```
[ ViewModel ]──→[ UseCase ]──→[ Repository (interface in :core:domain) ]
                                          ▲
                                          │ implements
                                          │
                                [ RepositoryImpl ]──→[ <X>Api ]──→[ Ktor HttpClient ]
                                                       │
                                                       └→ <X>Dto / Add<X>Request (@Serializable)
                                  domain Param → wire Request via toRequest()
                                  wire Dto      → domain via toDomain()
```

## 1. The Ktor `HttpClient`

Engine is platform-bound (see **kmp-platform**); the Ktor `HttpClient` itself is bound in
`:core:data/data/di/DataModule.kt`:

```kotlin
val dataModule = module {
    single { provideHttpClient(engine = get(), settings = get()) }
    singleOf(::<X>Api)
    singleOf(::<X>RepositoryImpl) bind <X>Repository::class
    // … one Api + one RepositoryImpl pair per domain
}

private fun provideHttpClient(engine: HttpClientEngine, settings: Settings): HttpClient =
    HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; explicitNulls = false }) }
        install(Logging) { level = LogLevel.INFO }
        install(HttpTimeout) { requestTimeoutMillis = 15_000 }
        defaultRequest {
            url(settings.getString(KEY_BASE_URL, ""))
            settings.getStringOrNull(KEY_AUTH_TOKEN)?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
        install(HttpRequestRetry) { retryOnExceptionOrServerErrors(maxRetries = 2); exponentialDelay() }
    }
```

- `expectSuccess = true` makes non-2xx responses throw `ClientRequestException` /
  `ServerResponseException` — the repository can catch and map them to typed `AppException`
  subclasses (see **kmp-error-handling**).
- Settings come from `multiplatform-settings`, bound per platform in `:composeApp/<plat>Main`'s
  Koin module.

For **multi-tenant** apps (one Mongo DB / one base URL per `clientId`), the `defaultRequest`
reads `KEY_BASE_URL` from settings — switching tenant means writing a new value and rebuilding
no client.

## 2. The `<X>Api` class

Plain Ktor calls. Takes DTO/Request directly, returns DTOs:

```kotlin
// core/data/src/commonMain/kotlin/<base>/data/remote/api/CustomerApi.kt
class CustomerApi(private val http: HttpClient) {

    suspend fun list(): List<CustomerDto> =
        http.get("api/v1/customers").body()

    suspend fun add(request: AddCustomerRequest): CustomerDto =
        http.post("api/v1/customers") { setBody(request) }.body()

    suspend fun update(id: String, request: UpdateCustomerRequest) {
        http.put("api/v1/customers/$id") { setBody(request) }
    }

    suspend fun delete(id: String) {
        http.delete("api/v1/customers/$id")
    }
}
```

- No `try/catch` here. Let Ktor throw; the repository decides what to do (typically pass it up
  to `BaseUseCase`'s `runCatching`).
- One method per endpoint. URL paths are inline string literals — they're not magic numbers,
  they're contract identifiers.
- Header overrides for special endpoints go in `block: HttpRequestBuilder.() -> Unit`.

## 3. The DTOs — `@SerialName` + camelCase

```kotlin
// core/data/src/commonMain/kotlin/<base>/data/remote/dto/CustomerDto.kt
@Serializable
data class CustomerDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("price_tier") val priceTier: String = "",
    @SerialName("allergy_note") val allergyNote: String? = null,
)

@Serializable
data class AddCustomerRequest(
    @SerialName("name") val name: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("price_tier") val priceTier: String? = null,
    @SerialName("allergy_note") val allergyNote: String? = null,
)
```

Rules (audit-enforced A24/A25):
- **Every** `@Serializable` property has an explicit `@SerialName("…")` — even when the wire
  name matches the Kotlin name. This makes the wire contract obvious and lets us rename Kotlin
  fields without breaking serialization.
- Kotlin name is **always camelCase**; the snake_case (or whatever the wire uses) lives in
  `@SerialName`. **A diff that adds a `val foo_bar` Kotlin name fails the audit.**
- Single-line format: `@SerialName("…") val foo: T = default,`. Defaults make optional fields
  forgiving.
- Don't migrate legacy embedded DTOs into the Kotlin name — keep the old shape as a separate
  type that the impl reads, then map to the new domain model.

## 4. The `RepositoryImpl`

Implements the **domain** interface. Maps DTO ↔ domain at the file bottom. **Domain never sees
DTOs.**

```kotlin
// core/data/src/commonMain/kotlin/<base>/data/repository/CustomerRepositoryImpl.kt
class CustomerRepositoryImpl(private val api: CustomerApi) : CustomerRepository {

    override suspend fun list(): List<Customer> =
        api.list().map { it.toDomain() }

    override suspend fun add(param: AddCustomerParam): Customer =
        api.add(param.toRequest()).toDomain()

    override suspend fun update(param: UpdateCustomerParam) {
        api.update(param.id, param.toRequest())
    }

    override suspend fun delete(id: String) {
        api.delete(id)
    }
}

private fun CustomerDto.toDomain(): Customer = Customer(
    id = id,
    name = name,
    phone = phone,
    priceTier = priceTier,
    allergyNote = allergyNote,
)

private fun AddCustomerParam.toRequest(): AddCustomerRequest = AddCustomerRequest(
    name = name,
    phone = phone,
    priceTier = priceTier,
    allergyNote = allergyNote,
)

private fun UpdateCustomerParam.toRequest(): UpdateCustomerRequest = UpdateCustomerRequest(
    name = name,
    phone = phone,
    priceTier = priceTier,
    allergyNote = allergyNote,
)
```

Rules:
- **No `runCatching`/`try/catch` in repo impls** — let typed exceptions propagate; the
  `BaseUseCase` wraps once. (Known exceptions exist for use cases that need to recover mid-flow,
  but **never** at the repository level.)
- Return **bare `T`**, not `Result<T>`.
- Mapping functions are file-private (`private fun X.toDomain()`) at the file bottom — same file
  as the impl. Don't extract them to a `Mappers.kt`; keep them next to the only caller.
- A repository impl that touches **two** APIs (e.g. read from Mongo cache then fall through to
  HTTP) is allowed, but composing repos is not — keep the repository the boundary.

## 5. DI binding

```kotlin
// core/data/src/commonMain/kotlin/<base>/data/di/DataModule.kt
val dataModule = module {
    single { provideHttpClient(get(), get()) }
    singleOf(::CustomerApi)
    singleOf(::CustomerRepositoryImpl) bind CustomerRepository::class
    // … more pairs
}
```

`:composeApp/di/AppModule.kt` `includes(commonModule, domainModule, dataModule, + every feature
module)` — `dataModule` is included in `:composeApp` only (audit A20: features can't see it).

## 6. Testing the data layer

- **API tests** (in `:core:data/commonTest`) use Ktor's `MockEngine`:
  ```kotlin
  val mockEngine = MockEngine { request ->
      when (request.url.encodedPath) {
          "/api/v1/customers" -> respond(
              content = Json.encodeToString(testCustomers),
              headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
          )
          else -> respondError(HttpStatusCode.NotFound)
      }
  }
  val client = HttpClient(mockEngine) { install(ContentNegotiation) { json() } }
  ```
- **Repository tests** assert the DTO↔domain mapping (round-trip on a representative DTO) and
  that the impl translates errors correctly (a `ServerResponseException` becomes a
  `ServerException`, etc. — see **kmp-error-handling**).
- **Feature tests** never use a `RepositoryImpl`; they use a `Fake<X>Repository` against the
  domain interface (see **kmp-test**).

## 7. Anti-patterns to flag

- **`@Serializable` without `@SerialName`** on every property → audit A24, fail.
- **snake_case Kotlin name** in a DTO → audit A25, fail.
- **Repository impl wrapping calls in `runCatching`/`try/catch`** → typed exceptions get
  swallowed; let them propagate.
- **DTO leaking out of `:core:data`** — a `@Serializable` type imported into `:core:domain` or
  `:features:*` → fail.
- **`:features:*` importing `:core:data`** → audit A20, fail. Use the `:core:domain` repository
  interface.
- **Endpoint URL constructed from user input without sanitization** → security review.
- **`expectSuccess = false`** on the shared `HttpClient` — masks errors, makes the contract
  unclear. Only override per-call when you genuinely want to inspect a non-2xx body.
