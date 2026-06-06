---
name: pharmacy-kmp-feature
description: Scaffold a new end-to-end feature in the pharmacy-app KMP companion — domain (model/param/repository/usecase) → data (dto/api/repositoryimpl) → a new :features:<x> module (Screen/Content/Callbacks/ViewModel/UiState/NavGraph) → DI bindings → :features:shared Route → wire from :composeApp. Use when adding a brand-new feature/domain to /Users/admin/ProjectPos/pharmacy-app/app-kmp.
---

# pharmacy-kmp-feature

Scaffold a vertical slice across the 27-module Clean-Architecture layout. Deps flow
inward only: `:composeApp → :features:<x> → :features:shared → :core:ui → :core:domain → :core:common`,
with `:core:data` bound only inside `:composeApp`.

**No code comments anywhere** (`.kt`, including KDoc/TODO). Self-documenting names only.

## Layer-by-layer recipe

### 1. Domain — `:core:domain`
- `domain/model/<X>.kt` — pure `data class`, no annotations.
- `domain/param/<feature>/<Op>Param.kt` — **only when an op takes 2+ inputs**; single-arg ops stay plain.
- `domain/repository/<feature>/<X>Repository.kt` — interface, methods return **bare `T`** (not `Result<T>`), throw typed `AppException`:
  ```kotlin
  interface CustomerRepository {
      suspend fun list(): List<Customer>
      suspend fun add(param: AddCustomerParam): Customer
  }
  ```
- `domain/usecase/<feature>/<Op>UseCase.kt` — extend `BaseUseCase<P, R>` (from `:core:common`, package `app.devper.pharm.domain.usecase`):
  ```kotlin
  class GetCustomersUseCase(
      private val customers: CustomerRepository,
      dispatchers: AppDispatchers,
  ) : BaseUseCase<Unit, List<Customer>>(dispatchers) {
      override suspend fun execute(param: Unit): List<Customer> = customers.list()
      suspend operator fun invoke(): Result<List<Customer>> = invoke(Unit)
  }
  ```
  `BaseUseCase.invoke` runs in `dispatchers.io` and wraps once in `runCatching` → callers get `Result<R>`. Repos never wrap.
- Register factories in `domain/di/<Feature>DomainModule.kt` (`factoryOf(::GetCustomersUseCase)`); if new, create the file and add it to `DomainModule.kt`'s `includes(...)`. There are 12 such sub-modules.

### 2. Data — `:core:data`
- `data/remote/dto/<X>Dto.kt` + `Add<X>Request.kt` — `@Serializable`, **camelCase Kotlin name + explicit `@SerialName` on every field** (A24/A25), single-line:
  ```kotlin
  @SerialName("sell_price") val sellPrice: Double = 0.0,
  ```
- `data/remote/api/<X>Api.kt` — Ktor calls, takes DTO/Request directly.
- `data/repository/<X>RepositoryImpl.kt` — implements the domain interface; maps DTO↔domain with `private fun toDomain(d: <X>Dto)` and `private fun <Op>Param.toRequest()` at file bottom. Domain never sees DTOs.
- Bind in `data/di/DataModule.kt`: `singleOf(::<X>Api)` + `singleOf(::<X>RepositoryImpl) bind <X>Repository::class`.

### 3. New module + Route
Follow the per-feature recipe (`:features:help` is the smallest template):
1. `mkdir -p features/<feat>/src/commonMain/kotlin/app/devper/pharm/presentation/<feat>/`
2. `features/<feat>/build.gradle.kts` applying `id("pharmacy.kmp.compose.library")`, deps `:core:domain` + `:core:ui` + `:features:shared`.
3. Append `:features:<feat>` to `settings.gradle.kts` `include(...)`.
4. Add the route to `:features:shared`: `features/shared/.../presentation/<feat>/<Feat>Routes.kt` → `@Serializable data object <Feat>` (+ `data class <Feat>Edit(val id: String)` for detail routes).

### 4. Presentation — `:features:<feat>` (the 6 files)
- **`<X>UiState.kt`** — immutable `data class … : BaseUiState` with `override val loading`, `override val error: String?`, plus screen fields.
- **`<X>Callbacks.kt`** — `data class` of lambdas, all defaulted to no-op (`val onRowClick: (X) -> Unit = {}`).
- **`<X>ViewModel.kt`** — `class … : BaseViewModel<<X>UiState>(<X>UiState())`, constructor-inject use cases (+ `Provider`s for shared state), call `launchResult(block = { useCase() }, onSuccess = { setState { copy(...) } }, onFailure = { setState { copy(error = it.message) } })`. Expose `fun dismissError() = setState { copy(error = null) }`. **Depend on use cases only — never repositories/APIs/`:core:data`.**
- **`<X>Content.kt`** — stateless `@Composable(state, callbacks)`, `val t = pharmTokens`, uses `Pharm*` primitives (no M3 in net-new), renders loading/empty/data, ends with `ErrorBottomSheet(state.error, callbacks.onDismissError)`. Add `@Preview` variants (loaded/loading/empty).
  - **List/dashboard pages** use `PharmListToolbar(title, subtitle, searchValue, filters, actions)` as the toolbar and `PharmListResultLine(total, noun, trailing)` for the count band. Empty state → `PharmEmptyState(icon, title, subtitle)`.
  - **Sub-pages** (detail/form/history) wrap the body in `PharmSubPage(title, onBack, subtitle?, actions?, bottomBar?, scrollable?, contentPadding?, contentSpacing?)` instead of building their own back-header. Form sections inside go in `PharmFormCard(title)`; forms put `PharmSaveAction(saving, canSubmit, onSubmit)` in `actions` (no bottom save bar).
- **`<X>Screen.kt`** — `@Composable fun <X>Screen(vm: <X>ViewModel = koinViewModel())` → `val state by vm.state.collectAsStateWithLifecycle()` then `<X>Content(state, <X>Callbacks(onRowClick = vm::..., ...))`. **Always `collectAsStateWithLifecycle`** (battery). **List/dashboard screens** add `ReloadOnResume(vm::reload)` (from `:core:ui/ui/common/`) so a record added on a detail page reflects when you return.
- **`<Feat>NavGraph.kt`** — `fun NavGraphBuilder.<feat>Graph(navController, onLogout, pendingSyncCount, role, user, onNavigateMain, onProfileClick)`:
  - **Top-level (sidebar entry)**: `composable<<Feat>> { ShelledScreen(title = "ไทย", ...) { <X>Screen(onOpenDetail = { navController.navigate(<Feat>Detail(it.id)) }) } }`.
  - **Sub-pages** render bare (no `ShelledScreen`, no sidebar): `composable<<Feat>Detail> { entry -> <X>DetailScreen(onBack = { navController.popBackStack() }, ...) }` — the sub-page's `PharmSubPage` carries its own back arrow.
- `di/<Feat>Module.kt` — **VM factories only** (A23): `val <feat>Module = module { factoryOf(::<X>ViewModel) }`.

### 5. Wire from `:composeApp`
- `composeApp/build.gradle.kts`: `implementation(project(":features:<feat>"))`.
- `di/AppModule.kt`: append `<feat>Module` to `includes(...)`.
- `presentation/navigation/AppNavHost.kt`: append `<feat>Graph(navController, …)`.
- `features/shared/.../navigation/ShelledScreen.kt`: add to `MAIN_NAV_TABLE` if it gets a sidebar item.

### 6. Test
Use `pharmacy-kmp-test`. Every VM ships a `<X>ViewModelTest.kt`.

## Verify
```bash
./gradlew :composeApp:auditArchitecture :features:<feat>:jvmTest \
          :composeApp:testDebugUnitTest \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs
```
`AppModuleWiringTest` in `:composeApp` will fail if a `factoryOf` binding is missing.

## Gotchas
- Typo-free here, but the **Go** services use `featues`/`catagory` deliberately — irrelevant to KMP.
- A23: `di/<Feat>Module.kt` may import **only VM types** — no use cases, repos, models.
- A20: features must not import `:core:data` — use the `:core:domain` repository interface.
- A26/A27: no platform source folders and no `expect`/`actual` outside `:composeApp`.
