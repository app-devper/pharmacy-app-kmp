---
name: kmp-feature
description: Scaffold a vertical slice in the pharmacy app — domain (model/param/repository/usecase) → :core:data (dto/api/repositoryimpl) → :features:<x> (Screen/Content/Callbacks/ViewModel/UiState + navigation + errors + i18n) → DI → wire from :composeApp. Use when adding a brand-new feature.
---

# kmp-feature

The recipe for a new vertical slice. Read `kmp-code-pattern` first for the
module rules; this is the step-by-step that applies them.

Deps flow inward only:
`:composeApp → :features:<x> → :core:ui → :core:domain → :core:common`,
with `:core:data` bound only inside `:composeApp`. No comments anywhere.

Placeholders: `<feat>` = lowercase feature (e.g. `customers`), `<Feat>` =
PascalCase (`Customers`), `<X>` = the entity (`Customer`). Base package is
`app.devper.pharm`.

## 1. Domain — `:core:domain`

- `domain/model/<X>.kt` — pure `data class`, no annotations. Money fields are
  `Money`, counted-stock fields are `Quantity` (`:core:common/value/`).
- `domain/param/<feat>/<X>Input.kt` / `<Op>Param.kt` — only when an op takes
  2+ inputs; single-argument ops pass the value directly.
- `domain/repository/<feat>/<X>Repository.kt` — interface returning **bare
  `T`**, never `Result<T>`; failures are thrown typed `AppException`s:
  ```kotlin
  interface CustomerRepository {
      suspend fun list(): List<Customer>
      suspend fun add(input: CustomerInput): Customer
      suspend fun update(id: String, input: CustomerInput)
  }
  ```
- `domain/usecase/<feat>/<Op>UseCase.kt` — one of the four bases from
  `:core:common` (package `app.devper.pharm.domain.usecase`):

  | Base | Shape |
  |---|---|
  | `BaseUseCase<P, R>` | suspend, takes a param |
  | `BaseQueryUseCase<R>` | suspend, no param — `invoke()` |
  | `BaseSyncUseCase<P, R>` | pure, takes a param |
  | `BaseSyncQueryUseCase<R>` | pure, no param |

  ```kotlin
  class GetCustomersUseCase(
      private val customers: CustomerRepository,
      dispatchers: AppDispatchers,
  ) : BaseQueryUseCase<List<Customer>>(dispatchers) {
      override suspend fun execute(param: Unit): List<Customer> = customers.list()
  }
  ```
  The base does `withContext(dispatchers.io) { runCatching { execute(param) } }`
  — that is the **only** place `Result` is created. Never wrap again.
- `domain/di/<Feat>DomainModule.kt` — `factoryOf(::GetCustomersUseCase)` etc.,
  then add it to `DomainModule.kt`'s `includes(...)`.

## 2. Data — `:core:data`

- `data/remote/dto/<X>Dto.kt` — `@Serializable`, camelCase Kotlin name +
  **explicit `@SerialName` on every field**, one line each (A24/A25):
  ```kotlin
  @SerialName("sell_price") val sellPrice: Double = 0.0,
  ```
  DTOs keep `Double`/`Int` — the wire format doesn't know about `Money`.
- `data/remote/api/<X>Api.kt` — Ktor calls, DTOs in and out.
- `data/repository/<X>RepositoryImpl.kt` — implements the domain interface and
  maps DTO ↔ domain in `private fun` at the bottom of the file, wrapping
  inbound values (`Money(dto.sellPrice)`) and unwrapping outbound
  (`price.amount`). **Domain never sees a DTO.**
- Bind in `data/di/DataModule.kt`:
  `singleOf(::CustomerApi)` + `singleOf(::CustomerRepositoryImpl) bind CustomerRepository::class`.

## 3. The `:features:<feat>` module

```bash
mkdir -p features/<feat>/src/commonMain/kotlin/app/devper/pharm/presentation/<feat>
mkdir -p features/<feat>/src/commonTest/kotlin/app/devper/pharm/presentation/<feat>
```

`features/<feat>/build.gradle.kts`:

```kotlin
plugins {
    id("pharmacy.kmp.compose.library")
    alias(libs.plugins.kotlin.serialization)      // ← the route serializer needs this
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:ui"))
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(project(":features:test-fixtures"))
        }
    }
}

android { namespace = "app.devper.pharm.features.<feat>" }
```

Then add `:features:<feat>` to `settings.gradle.kts`.

## 4. Presentation — file-per-class

```
features/<feat>/src/commonMain/kotlin/app/devper/pharm/
  di/<Feat>Module.kt                          ← package app.devper.pharm.di, VMs only
  presentation/<feat>/
    <X>UiState.kt            data class : BaseUiState — loading + errorState + fields + derived gets
    <X>Callbacks.kt          data class of lambdas, each defaulted to {}
    <X>ViewModel.kt          class : BaseViewModel<<X>UiState> — use cases only
    <X>Content.kt            stateless @Composable(state, callbacks) + ≥3 @Preview
    <X>Screen.kt             stateful @Composable wiring koinViewModel() → Content
    <X>Table.kt / <X>Toolbar.kt / …            further stateless splits
    exception/<X>UiStateError.kt               sealed AppException subclasses
    i18n/<X>UiStateErrorLocalize.kt            fun AppException.localize<X>(s: PharmStrings)
    navigation/<Feat>Nav.kt                    @Serializable routes + NavGraphBuilder.<feat>Nav(…)
    form/                                      form Content/Callbacks/sections, when there is one
```

Note the DI module sits in `app.devper.pharm.di`, **not** under
`presentation/<feat>/` — that is what A23 scans.

Key rules:
- One concept per file. A file holding two of {Screen, Content, ViewModel,
  UiState} is a defect.
- `di/<Feat>Module.kt` imports only ViewModel types (A23 fails the build if it
  imports a use case, observer or parser).
- ViewModels depend on use cases only — never repositories, APIs or `:core:data`
  (A20).
- Screens use `collectAsStateWithLifecycle()`, never `collectAsState()`.
- List/dashboard Screens call `ReloadOnResume(vm::reload)`.
- Forms use `BaseFormViewModel` — see `kmp-add-form`.
- Errors are typed end-to-end and localized at render — see
  `kmp-error-handling`.
- Every user-visible string comes from `pharmStrings`; add keys to the group
  interface + `Th` + `En` objects in `:core:ui/i18n/groups/` (A29).

## 5. Wire from `:composeApp`

- `composeApp/build.gradle.kts`: `implementation(project(":features:<feat>"))`
- `di/AppModule.kt`: append `<feat>Module` to `includes(...)`
- `navigation/MainNav.kt`: call `<feat>Nav(nestedNav)` inside the nested
  NavHost, add a `DEST_INFO` entry (title + sidebar `sectionKey`) per
  destination, and a sidebar row if the feature gets one.
- Cross-feature jumps go through a hoisted `() -> Unit` resolved in
  `:composeApp` — never by importing another feature's route.

## 6. Test

`kmp-test` has the `runVmTest` + `Fake<X>Repository` pattern. Every ViewModel
ships a `<X>ViewModelTest.kt` in `:features:<feat>:commonTest`.

## Verify

```bash
./gradlew :composeApp:auditArchitecture :features:<feat>:jvmTest \
          :composeApp:testDebugUnitTest \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs
```

`composeApp/src/commonTest/…/di/AppModuleWiringTest.kt` instantiates every
ViewModel through the real DI graph — it is what catches a missing or
mistyped `factoryOf`.

## Common gotchas

- **Missing `kotlin.serialization` plugin** on the new module → the route
  compiles but throws `SerializationException` at runtime.
- **Cross-feature import** — `:features:<x>` reaching into `:features:<y>`.
  Hoist a callback to `:composeApp` instead.
- **Combined files** — `Screen.kt` also holding the ViewModel or UiState.
- **A ViewModel typed against a repository** instead of use cases.
- **A DI module importing a use case** (A23).
- **A DTO field without `@SerialName`** or with a snake_case Kotlin name
  (A24/A25).
- **A platform source folder** anywhere but `:composeApp` (A26), or any
  `expect` declaration (A27).
