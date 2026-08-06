---
name: kmp-code-pattern
description: The Clean-Architecture + MVVM code pattern for a Compose Multiplatform project — module layering, repository/use-case/view-model contracts, error handling, DI bindings, and per-feature file structure. Use when adding a vertical slice, designing modules for a new KMP project, or auditing layering violations.
---

# kmp-code-pattern

The code pattern is **inward-only Clean Architecture + MVVM**. Memorize the data-flow direction
and the file-per-class rule; everything else falls out.

```
[ Screen ]──→[ ViewModel ]──→[ UseCase ]──→[ Repository (interface) ]──→[ RepositoryImpl ]──→[ Api / Storage ]
   ↑state    setState{copy}    Result<T>          throws AppException             DTO ↔ domain mapping
```

## 1. Module layout

| Module | Owns | Depends on |
|---|---|---|
| `:core:common` | dispatchers, logger, `AppException` subclasses, `BaseUseCase` framework | kotlinx only (no project deps) |
| `:core:domain` | models, params, repository **interfaces**, use cases, DI sub-modules | `:core:common` |
| `:core:ui` | tokens, primitives, `BaseViewModel`, `BaseFormViewModel`, `BaseUiState`, `runVmTest` | `:core:domain` (compose) |
| `:core:data` | DTOs, APIs, `RepositoryImpl`s, transport (ktor/http/storage) | `:core:domain` |
| `:features:<x>` | Screen / Content / Callbacks / ViewModel / UiState / NavGraph + DI VM bindings | `:core:domain` + `:core:ui` |
| `:features:test-fixtures` | shared `Fake<X>Repository`s (commonMain, not commonTest) | `:core:common` + `:core:domain` + kotlinx |
| `:composeApp` | entry point, app shell, navigation, DI composition root | **only place** with platform folders + `:core:data` access |

Forbidden:
- `:features:<x>` importing `:core:data`, another `:features:<y>` (cross-feature), or `:composeApp`.
- Any module other than `:composeApp` having platform source folders (`androidMain`/`iosMain`/`jvmMain`/`wasmJsMain`).
- `expect`/`actual` declarations anywhere (use an interface in `:core:common` + impls in `:composeApp/<plat>Main`).

## 2. Per-feature files — the file-per-class rule

Every feature has **exactly these files** (one class/composable per file):

```
features/<feat>/src/commonMain/.../presentation/<feat>/
  <X>UiState.kt          — data class : BaseUiState
  <X>Callbacks.kt        — data class of lambdas, all defaulted to {}
  <X>ViewModel.kt        — class : BaseViewModel<<X>UiState>(…)
  <X>Content.kt          — stateless @Composable (state, callbacks) + @Preview variants
  <X>Screen.kt           — stateful @Composable wiring koinViewModel() → Content
  navigation/<Feat>Nav.kt — @Serializable routes + fun NavGraphBuilder.<feat>Nav(...)
  di/<Feat>Module.kt      — VM factoryOf bindings ONLY
```

Audit rules to enforce:
- A file containing 2+ of {Screen, Content, ViewModel, UiState} → fail. Each must live alone.
- `di/<Feat>Module.kt` importing a non-VM type → fail (only `factoryOf(::…ViewModel)` allowed).
- A Screen calling `koinInject()` on a UseCase or Repository → fail (MVVM violation).

## 3. The contracts

### Repository — `:core:domain/repository/<feature>/<X>Repository.kt`
```kotlin
interface CustomerRepository {
    suspend fun list(): List<Customer>                 // bare T, not Result<T>
    suspend fun add(param: AddCustomerParam): Customer // throws typed AppException on failure
}
```

### Use case — `:core:domain/usecase/<feature>/<Op>UseCase.kt`
```kotlin
class GetCustomersUseCase(
    private val customers: CustomerRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<Unit, List<Customer>>(dispatchers) {
    override suspend fun execute(param: Unit): List<Customer> = customers.list()
    suspend operator fun invoke(): Result<List<Customer>> = invoke(Unit)
}
```
`BaseUseCase.invoke` runs in `dispatchers.io` and wraps once in `runCatching` → callers get
`Result<R>`. Repositories never wrap, never `try/catch`.

### ViewModel — `:features:<feat>/.../<X>ViewModel.kt`
```kotlin
class CustomersListViewModel(
    private val getCustomers: GetCustomersUseCase,
) : BaseViewModel<CustomersListUiState>(CustomersListUiState()) {

    init { reload() }
    fun dismissError() = setState { copy(error = null) }

    fun reload() {
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getCustomers() },
            onSuccess = { list -> setState { copy(loading = false, customers = list) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดข้อมูลไม่สำเร็จ") } },
        )
    }
}
```
ViewModels depend on **use cases only** (+ shared `Provider`s for cross-feature state) — never
repositories, APIs, or `:core:data`. State updates go through `setState { copy(...) }`. Background
work goes through `launchResult(block, onSuccess, onFailure[, withLoading])` (provided by
`BaseViewModel`).

### Form ViewModel — `BaseFormViewModel<S>`
Forms use the F-bounded subclass so `submit()` is provided: it no-ops when `!canSubmit`, sets
`saving=true`, runs `persist()`, lands on `saved=true` or `error=...`. See **kmp-add-form** skill.

### State
```kotlin
data class CustomersListUiState(
    override val loading: Boolean = false,
    val query: String = "",
    val customers: List<Customer> = emptyList(),
    override val error: String? = null,
) : BaseUiState {
    val filtered: List<Customer>
        get() = if (query.isBlank()) customers else customers.filter { it.name.contains(query) }
}
```
Immutable data class. Implements `BaseUiState` (which requires `loading` + `error`). Derived
values are read-only `get()` properties — no caching, no `var`.

## 4. Errors

- Repos return bare `T`; they throw **typed** `AppException` subclasses on failure (`AuthException`,
  `NotFoundException`, `ConflictException`, `NetworkException`, `ServerException`,
  `ValidationException`, `StorageException`, …).
- `BaseUseCase` catches once via `runCatching` → `Result<R>`. **Do not** add `runCatching` inside
  repository impls or use cases.
- `launchResult` in the VM surfaces `state.error: String?`. Every screen renders an
  `ErrorBottomSheet(state.error, callbacks.onDismissError)`.
- **Never** throw generic `Exception`/`RuntimeException`/`IllegalStateException` in production
  code. Fakes in `:features:test-fixtures` are the only exception (deliberate test signal).

## 5. DI (Koin)

- `:core:domain/di/<Feature>DomainModule.kt` — `factoryOf(::GetCustomersUseCase)`. Aggregate
  feature modules under `:core:domain/di/DomainModule.kt`'s `includes(...)`.
- `:core:data/di/DataModule.kt` — `singleOf(::CustomerApi)` + `singleOf(::CustomerRepositoryImpl) bind CustomerRepository::class`.
- `:features:<feat>/di/<Feat>Module.kt` — VMs only: `factoryOf(::CustomersListViewModel)`.
- `:composeApp/di/AppModule.kt` — composition root: `includes(commonModule, domainModule,
  dataModule, + every feature module)`. Add the new feature here when wiring.

## 6. Data layer (DTO ↔ domain)

- `:core:data/data/remote/dto/<X>Dto.kt` and `Add<X>Request.kt` — `@Serializable`, camelCase
  Kotlin field + explicit `@SerialName("snake_case")` on every property:
  ```kotlin
  @SerialName("sell_price") val sellPrice: Double = 0.0,
  ```
- `:core:data/data/remote/api/<X>Api.kt` — Ktor calls returning DTOs.
- `:core:data/data/repository/<X>RepositoryImpl.kt` — implements the domain interface; maps
  DTO↔domain with `private fun toDomain(d: <X>Dto)` and `private fun <Op>Param.toRequest()` at
  file bottom. **Domain never sees DTOs.**

## 7. Audit — the build-enforced rules

A `pharmacy.architecture.audit` Gradle task should grep for:
- inward-only layering violations (`:core:*` importing `presentation`, `:features:*` importing
  `:core:data`)
- DTO discipline (every `@Serializable` property has `@SerialName` + camelCase Kotlin name)
- platform-folder ownership (only `:composeApp` has `<plat>Main`)
- no `expect`/`actual` anywhere
- no generic exceptions in production
- `:features:<x>/di/<X>Module.kt` imports only VM types

See **kmp-review** for the full list and severity classification.

## 8. Verify

```bash
./gradlew :composeApp:auditArchitecture \
          :composeApp:testDebugUnitTest \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs \
          :core:common:jvmTest :core:domain:jvmTest :core:ui:jvmTest :core:data:jvmTest
```
Plus `:features:<changed>:jvmTest` per touched feature module.

## Recognise "looks-wrong-but-is-right"

- `BaseUseCase` may live in `:core:common` under package `<base>.domain.usecase` (split package — intentional, so domain can `api(:core:common)` it).
- Repositories return bare `T` (no `Result<T>`) and have no `runCatching` — intentional.
- Use cases take `AppDispatchers`; ViewModels do **not** (they use `viewModelScope`).
- Test-fixture fakes throw `RuntimeException` — deliberate test signal, A28-exempt.
- Routes are `@Serializable` (kotlin.serialization plugin required on every feature module that
  declares one — a common foot-gun: route compiles but no serializer generated at runtime).
