---
name: kmp-code-pattern
description: The Clean-Architecture + MVVM contracts of the pharmacy app — module layering, repository/use-case/view-model shapes, the three ViewModel bases, DI wiring, and the build-enforced audit rules. Use when adding a vertical slice or auditing a layering violation.
---

# kmp-code-pattern

Inward-only Clean Architecture + MVVM. Learn the data-flow direction and the
file-per-class rule; everything else follows.

```
[ Screen ]──→[ ViewModel ]──→[ UseCase ]──→[ Repository interface ]──→[ RepositoryImpl ]──→[ Api / Storage ]
   ↑state    setState{copy}   Result<T>        throws AppException          DTO ↔ domain mapping
```

## 1. Modules (26 Gradle projects)

| Module | Owns | Depends on |
|---|---|---|
| `:core:common` | `AppDispatchers`, logger, `AppException` hierarchy, the `BaseUseCase` framework, `Money`/`Quantity` | kotlinx only — **zero project deps** |
| `:core:domain` | models, params, repository **interfaces**, use cases, validation, domain DI | `:core:common` |
| `:core:ui` | tokens, `Pharm*` primitives, `BaseViewModel` family, `PharmStrings`, formatting, `runVmTest` | `:core:common` + `:core:domain` |
| `:core:data` | DTOs, Apis, `RepositoryImpl`s, Ktor + storage, data DI | `:core:common` + `:core:domain` |
| `:features:<x>` (20) | Screen / Content / Callbacks / ViewModel / UiState / errors / i18n / nav / DI | `:core:domain` + `:core:ui` |
| `:features:test-fixtures` | shared `Fake<X>Repository`s in **commonMain** | `:core:common` + `:core:domain` |
| `:composeApp` | entry point, `AppShell`, navigation, DI composition root | everything — the **only** module with platform folders and `:core:data` access |

Forbidden (all build-enforced, see §6):
- `:core:*` importing `presentation.*` (A10)
- `:features:<x>` importing `:core:data` (A20), or another feature, or `:composeApp`
- platform source folders outside `:composeApp` (A26)
- any `expect` declaration anywhere (A27)

## 2. File-per-class

```
features/<feat>/src/commonMain/kotlin/app/devper/pharm/
  di/<Feat>Module.kt                    ← package app.devper.pharm.di — VM bindings only
  presentation/<feat>/
    <X>UiState.kt  <X>Callbacks.kt  <X>ViewModel.kt  <X>Content.kt  <X>Screen.kt
    <X>Table.kt / <X>Toolbar.kt / …     ← further stateless splits as pages grow
    exception/    i18n/    navigation/    form/
```

A file holding two of {Screen, Content, ViewModel, UiState} is a defect. A
Screen calling `koinInject()` on a use case or repository is an MVVM violation.

## 3. The contracts

### Repository — `:core:domain/repository/<feat>/<X>Repository.kt`

```kotlin
interface CustomerRepository {
    suspend fun list(): List<Customer>                    // bare T, never Result<T>
    suspend fun add(input: CustomerInput): Customer       // throws typed AppException
    suspend fun update(id: String, input: CustomerInput)
}
```

No `runCatching`, no `try/catch`. Failure is a thrown typed exception.

### Use case — `:core:domain/usecase/<feat>/<Op>UseCase.kt`

Four bases, all from `:core:common` (package `app.devper.pharm.domain.usecase`):
`BaseUseCase<P, R>`, `BaseQueryUseCase<R>` (no param), `BaseSyncUseCase<P, R>`
and `BaseSyncQueryUseCase<R>` (pure, no dispatcher).

```kotlin
class GetCustomersUseCase(
    private val customers: CustomerRepository,
    dispatchers: AppDispatchers,
) : BaseQueryUseCase<List<Customer>>(dispatchers) {
    override suspend fun execute(param: Unit): List<Customer> = customers.list()
}
```

`invoke()` does `withContext(dispatchers.io) { runCatching { execute(param) } }`.
That is the **only** place a `Result` is created — never wrap again downstream.

### ViewModel bases — `:core:ui/ui/common/`

| Base | For | Gives you |
|---|---|---|
| `BaseViewModel<S : BaseUiState>` | anything | `state`, `current`, `setState { copy(…) }`, `launchResult(block, onSuccess, onFailure, withLoading)` |
| `BaseLoadableViewModel<S : LoadableUiState<S>>` | list / detail pages | `+ dismissError()` |
| `BaseFormViewModel<S : BaseFormUiState<S>>` | forms | `+ submit()`, `dismissError()`, `resetSaved()`, `mapSaveError()` |

```kotlin
class CustomersListViewModel(
    private val getCustomers: GetCustomersUseCase,
) : BaseLoadableViewModel<CustomersListUiState>(CustomersListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun reload() {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getCustomers() },
            onSuccess = { list -> setState { copy(loading = false, customers = list) } },
            onFailure = { e ->
                setState { copy(loading = false, errorState = CustomersListUiStateError.LoadCustomersFailed(e)) }
            },
        )
    }
}
```

ViewModels take **use cases only** — never repositories, APIs or `:core:data`.
They do not take `AppDispatchers` (that's the use case's job) and they never
localize (that happens at render).

### State

```kotlin
data class CustomersListUiState(
    override val loading: Boolean = false,
    val query: String = "",
    val customers: List<Customer> = emptyList(),
    val errorState: AppException? = null,
) : LoadableUiState<CustomersListUiState> {

    override val domainError: AppException? get() = errorState
    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val filtered: List<Customer> = if (query.isBlank()) customers else customers.filter { … }
}
```

Immutable data class, derived values computed in the constructor body or as
`get()` properties. `errorState` is a typed `AppException?` — there is no
`error: String?` field anywhere in the project.

## 4. Errors

Repos throw typed → `BaseUseCase` wraps once → the VM stores a typed
`errorState` → the Content localizes at render with `localize<X>(pharmStrings)`
→ `ErrorBottomSheet`. Full detail in `kmp-error-handling`.

Never throw `Exception` / `RuntimeException` / `IllegalStateException` in
production (A28). `:features:test-fixtures` is exempt — its fakes throw on
purpose.

## 5. DI (Koin)

| File | Contents |
|---|---|
| `:core:domain/di/<Feat>DomainModule.kt` | `factoryOf(::GetCustomersUseCase)` — aggregated by `DomainModule.kt`'s `includes(...)` |
| `:core:data/di/DataModule.kt` | `singleOf(::CustomerApi)`, `singleOf(::CustomerRepositoryImpl) bind CustomerRepository::class` |
| `:features:<x>/…/di/<Feat>Module.kt` | `factoryOf(::CustomersListViewModel)` — ViewModels only (A23) |
| `:composeApp/di/AppModule.kt` | composition root: `includes(commonModule, domainModule, dataModule, + every feature module)` |

Platform bindings (printer, file IO, storage, HTTP engine) are declared in each
platform's `Main*.kt`, never with `expect`/`actual` — see `kmp-platform`.

## 6. What the build actually enforces

`./gradlew :composeApp:auditArchitecture` (also wired into `check`) —
`build-logic/src/main/kotlin/pharmacy.architecture.audit.gradle.kts`:

| Rule | Fails on |
|---|---|
| A10 | `core/**` importing `app.devper.pharm.presentation.*` |
| A17 | any import of the removed `app.devper.pharm.domain.common.*` |
| A19 | imports of stale pre-split `:core:ui` packages (`presentation.theme`, `presentation.designsystem`, `presentation.common.Base*`, …) |
| A20 | `features/**` importing `app.devper.pharm.data.*` |
| A23 | a `features/**/di/*Module.kt` importing `domain.usecase` / `domain.observer` / `domain.parser` |
| A24 | a `@Serializable` DTO property without `@SerialName` |
| A25 | a DTO property with a snake_case Kotlin name |
| A26 | a platform source folder outside `:composeApp` |
| A27 | any `expect class/fun/val/var/object/interface/typealias` |
| A28 | `throw` / `Result.failure(` of a generic exception in `core/**` or `features/**` production code |
| A29 | a Thai string literal in production UI code (`core/ui`, `core/domain`, `features`, `composeApp` commonMain) |

A29 skips `i18n/groups/`, `ui/print/`, files named `*Preview*`,
`:features:test-fixtures`, lines containing `.contains(`, everything from the
first `@Preview` / `private val sample*` marker onward, and a small
`a29AllowedFiles` allowlist of domain files with stored-data Thai defaults.

Note what is **not** enforced: raw Material 3 usage, hex colors outside
`theme/`, the file-per-class rule, and comments. Those are review-time — see
`kmp-review`.

## 7. Verify

```bash
./gradlew :composeApp:auditArchitecture \
          :composeApp:testDebugUnitTest \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs \
          :core:common:jvmTest :core:domain:jvmTest :core:ui:jvmTest :core:data:jvmTest \
          :features:<changed>:jvmTest
```

`koverVerify` enforces a line-coverage floor (`COVERAGE_FLOOR` in the root
`build.gradle.kts`) — raise it in the same PR when you add tests that push
coverage up.

## Recognise "looks-wrong-but-is-right"

- `BaseUseCase` lives in `:core:common` under package
  `app.devper.pharm.domain.usecase` — a deliberate split package so `:core:domain`
  can re-expose it.
- Repositories return bare `T` with no `runCatching`.
- Use cases take `AppDispatchers`; ViewModels do not.
- `:features:test-fixtures` fakes throw `RuntimeException` — A28-exempt on purpose.
- Routes are `@Serializable`, so every feature module declaring one must apply
  `alias(libs.plugins.kotlin.serialization)` or it throws at runtime, not compile time.
