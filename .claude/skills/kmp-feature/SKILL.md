---
name: kmp-feature
description: Scaffold a vertical slice in a Compose Multiplatform Clean-Architecture project — domain (model/param/repository/usecase) → data (dto/api/repositoryimpl) → :features:<x> (Screen/Content/Callbacks/ViewModel/UiState + navigation) → DI bindings → wire from :composeApp. Use when adding a brand-new feature/domain.
---

# kmp-feature

Scaffold a vertical slice across the Clean-Architecture layout. Read **kmp-code-pattern** first
for the module rules and contracts; this skill is the recipe that uses them.

Deps flow inward only:
`:composeApp → :features:<x> → :core:ui → :core:domain → :core:common`,
with `:core:data` bound only inside `:composeApp`. **No comments anywhere** (`.kt`, including
KDoc/TODO/FIXME) — self-documenting names instead.

> Replace placeholders: `<base>` = your base package (e.g. `app.devper.pharm`), `<feat>` =
> snake feature name (e.g. `customers`), `<X>` = PascalCase (e.g. `Customer`), `<Feat>` =
> PascalCase feature (e.g. `Customers`), `Brand*` = your design system prefix.

## Layer-by-layer recipe

### 1. Domain — `:core:domain`

- `domain/model/<X>.kt` — pure `data class`, no annotations.
- `domain/param/<feat>/<Op>Param.kt` — **only when an op takes 2+ inputs**; single-arg ops stay plain.
- `domain/repository/<feat>/<X>Repository.kt` — interface, methods return **bare `T`** (not
  `Result<T>`), throw typed `AppException`:
  ```kotlin
  interface CustomerRepository {
      suspend fun list(): List<Customer>
      suspend fun add(param: AddCustomerParam): Customer
  }
  ```
- `domain/usecase/<feat>/<Op>UseCase.kt` — extend `BaseUseCase<P, R>` from `:core:common`:
  ```kotlin
  class GetCustomersUseCase(
      private val customers: CustomerRepository,
      dispatchers: AppDispatchers,
  ) : BaseUseCase<Unit, List<Customer>>(dispatchers) {
      override suspend fun execute(param: Unit): List<Customer> = customers.list()
      suspend operator fun invoke(): Result<List<Customer>> = invoke(Unit)
  }
  ```
- Register factories in `domain/di/<Feat>DomainModule.kt` (`factoryOf(::GetCustomersUseCase)`);
  add the file to `DomainModule.kt`'s `includes(...)` if new.

### 2. Data — `:core:data`

- `data/remote/dto/<X>Dto.kt` + `Add<X>Request.kt` — `@Serializable`, **camelCase Kotlin name +
  explicit `@SerialName` on every field**, single-line:
  ```kotlin
  @SerialName("sell_price") val sellPrice: Double = 0.0,
  ```
- `data/remote/api/<X>Api.kt` — Ktor calls, takes DTO/Request directly.
- `data/repository/<X>RepositoryImpl.kt` — implements the domain interface; map DTO↔domain via
  `private fun toDomain(d: <X>Dto)` and `private fun <Op>Param.toRequest()` at file bottom.
  **Domain never sees DTOs.**
- Bind in `data/di/DataModule.kt`: `singleOf(::<X>Api)` + `singleOf(::<X>RepositoryImpl) bind <X>Repository::class`.

### 3. New `:features:<feat>` module

1. `mkdir -p features/<feat>/src/commonMain/kotlin/<base.path>/presentation/<feat>/`
2. `features/<feat>/build.gradle.kts` applying your KMP-compose convention plugin, deps:
   `:core:domain` + `:core:ui`. **Apply `kotlin.serialization` plugin** here so the route
   serializer is generated (a common foot-gun: the route compiles but throws
   `SerializationException` at runtime).
3. Append `:features:<feat>` to `settings.gradle.kts` `include(...)`.

### 4. Presentation — file-per-class (6 files)

```
features/<feat>/src/commonMain/kotlin/<base.path>/presentation/<feat>/
  <X>UiState.kt          — data class : BaseUiState (loading + error + screen fields + derived gets)
  <X>Callbacks.kt        — data class of lambdas, each defaulted to {}
  <X>ViewModel.kt        — class : BaseViewModel<<X>UiState>(…), inject use cases only
  <X>Content.kt          — stateless @Composable(state, callbacks) + ≥2 @Preview variants
  <X>Screen.kt           — stateful @Composable wiring koinViewModel() → Content
  navigation/<Feat>Nav.kt — @Serializable route objects + fun NavGraphBuilder.<feat>Nav(...)
  di/<Feat>Module.kt      — VM factoryOf bindings ONLY
```

Pattern templates: see **kmp-layout-pattern** (the `Column { BrandListToolbar ; content }`
structure) and **kmp-code-pattern** (the VM contract).

Key rules:
- **One concept per file.** A file containing 2+ of {Screen, Content, ViewModel, UiState} is a
  bug. Tests + audit should grep for this.
- **`di/<Feat>Module.kt` imports only VM types** — no use cases, repos, models.
- **VMs depend on use cases only** — never repositories/APIs/`:core:data`.
- **Screen always uses `collectAsStateWithLifecycle()`** (not `collectAsState`).
- **List/dashboard Screens call `ReloadOnResume(vm::reload)`** so a record added on a detail
  page reflects when the user navigates back.
- **Forms use `BaseFormViewModel`** — see **kmp-add-form**.

### 5. Wire from `:composeApp`

- `composeApp/build.gradle.kts`: `implementation(project(":features:<feat>"))`.
- `di/AppModule.kt`: append `<feat>Module` to `includes(...)`.
- `presentation/navigation/MainNav.kt` (or AppNavHost): call `<feat>Nav(nestedNav)` inside the
  nested NavHost; add a `DEST_INFO` entry (title + sidebar `sectionKey` per destination); add a
  `MAIN_NAV_TABLE` row if the feature gets a sidebar item.
- Cross-feature jumps go through **hoisted `() -> Unit` callbacks** resolved in composeApp — not
  by importing the other feature's route (which would break the layering rule).

### 6. Test

See **kmp-test** for the `runVmTest` + `Fake<X>Repository` pattern. **Every VM ships a
`<X>ViewModelTest.kt`** — covered by the per-feature audit.

## Verify

```bash
./gradlew :composeApp:auditArchitecture :features:<feat>:jvmTest \
          :composeApp:testDebugUnitTest \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs
```
An `AppModuleWiringTest` in `:composeApp` (instantiates every VM via the DI graph) catches
missing/wrong `factoryOf` bindings.

## Common gotchas

- **Missing `kotlin.serialization` plugin** on the new feature module → `@Serializable` route
  compiles but `SerializationException: Serializer for class '<Route>' is not found` at runtime.
- **Cross-feature dep** — `:features:<x>` importing `:features:<y>` is forbidden. Hoist a
  `() -> Unit` to `composeApp`.
- **Combined files** — `Screen.kt` containing both the VM and the State definition. Each must
  live in its own file (the file-per-class rule).
- **VM holding a repository/use-case property typed as `Repository<…>`/`Api<…>`** — VMs see use
  cases only.
- **`di/<Feat>Module.kt` importing a use case** — bindings file should only import
  `…ViewModel` types and `factoryOf` / `org.koin.dsl.module`.
