# PharmacyApp — Kotlin Multiplatform companion

Kotlin Multiplatform / Compose Multiplatform port of the React `frontend/` web POS. **One Kotlin codebase ships to Android, iOS, Desktop (JVM), and Web (wasmJs).** Shares the same backend (`pharmacy-app/backend`) and the same `um-api` auth.

Architecture is **6-module Gradle Clean Architecture** with inward-only deps; platform impls live in `:composeApp/<plat>Main/platform/` behind interfaces in `:core:common`; a convention plugin keeps every library's `build.gradle.kts` ~15–55 lines. **373 unit tests** cover business logic (241 VM tests + 71 domain + 33 ui + 20 data + 16 common + 1 wiring smoke).

See [`MODULE_GRAPH.md`](MODULE_GRAPH.md) for the full module dependency matrix.

## Stack

| Concern | Choice |
|---------|--------|
| Language | Kotlin 2.3.0 |
| UI | Compose Multiplatform 1.9.3 (Material 3 + custom design system) |
| Networking | Ktor 3.3 (engines per platform: OkHttp / Darwin / Java / Js) |
| DI | Koin 4.1 — each layer owns its own Koin module |
| Navigation | androidx.navigation-compose (multiplatform — type-safe `@Serializable` routes) |
| Storage | multiplatform-settings (UserDefaults / SharedPreferences / Preferences / localStorage) |
| Logging | Kermit |
| Build | Gradle 8.14 · AGP 8.13 · `pharmacy.kmp.library` convention plugin in `build-logic/` |

## Module structure (6 modules)

```
:composeApp                          entry point only (Main*.kt + AppNavHost + AppModule composer)
   │
   ▼  depends on every layer below ──────────────┐
:features                            ทุก 19 features รวมที่เดียว — Screens + VMs + per-feature DI
   │                                  package: app.devper.pharm.presentation.<feature>
   ▼  depends ONLY on :core:domain + :core:ui ───┤
:core:ui                             shared compose infra (theme + designsystem + BaseViewModel)
   │                                  package: app.devper.pharm.ui.*
   ▼                                              │
:core:domain                         pure domain — models / repos (interfaces) / use cases
   │                                  package: app.devper.pharm.domain.*
   ▼  api-exports :core:common ───────────────────┤
:core:data                           repository impls + ktor + storage
   │                                  package: app.devper.pharm.data.*
   ▼                                              │
:core:common                         interfaces + pure infra (commonMain + commonTest only)
                                      package: app.devper.pharm.common[.print|.platform]
                                      AppDispatchers + Logger + AppException + BaseUseCase
                                      + PdfDownloader (interface) + ReceiptPrinter (interface)
                                      — platform impls live in :composeApp/<plat>Main
build-logic/                         convention plugin `pharmacy.kmp.library` —
                                      KMP setup (5 targets · JDK 17 · namespace · commonTest kotlin.test)
                                      applied by every library module's build.gradle.kts
```

**Inward-only — Gradle-enforced**:
- `:core:common` has **zero** project deps (kotlinx-only)
- `:core:domain` → `:core:common` (api-exports it so consumers reach `AppDispatchers` / `AppException` / `printReceipt` / `BaseUseCase` transitively)
- `:core:ui` → `:core:domain` + `:core:common`
- `:core:data` → `:core:domain` + `:core:common`
- `:features` → `:core:domain` + `:core:ui` **only** (cannot reach `:core:data` at all; reaches `:core:common` transitively via `:core:domain`)
- `:composeApp` depends on every layer below

**One module owns platform code**: `:composeApp` is the **only** module with `androidMain` / `iosMain` / `jvmMain` / `wasmJsMain` source folders. `:core:common`, `:core:domain`, `:core:ui`, `:core:data`, and `:features` are `commonMain` + `commonTest` only — pure platform-agnostic code. Cross-platform seams (PDF saving, receipt printing, IO dispatcher) are expressed as **interfaces in `:core:common`** with **impls in `:composeApp/<plat>Main`**, bound via Koin in each Main*.kt's platform module. No `expect`/`actual` anywhere in the project.

## Where everything lives

| You want to add… | Module | Package | Path |
|---|---|---|---|
| Domain model | `:core:domain` | `app.devper.pharm.domain.model` | `core/domain/src/commonMain/kotlin/app/devper/pharm/domain/model/` |
| Domain param (`*Param`) | `:core:domain` | `app.devper.pharm.domain.param` | `core/domain/.../domain/param/<feature>/` (grouped per feature) |
| Repo interface | `:core:domain` | `app.devper.pharm.domain.repository` | `core/domain/.../domain/repository/<feature>/` |
| Use case | `:core:domain` | `app.devper.pharm.domain.usecase` | `core/domain/.../domain/usecase/<feature>/` + register in `di/<Feature>DomainModule.kt` |
| Provider (state observer) | `:core:domain` | `app.devper.pharm.domain.observer` | `core/domain/.../domain/observer/` + register in `di/<Feature>DomainModule.kt` |
| Repository impl / API / DTO | `:core:data` | `app.devper.pharm.data.<sub>` | `core/data/src/commonMain/kotlin/app/devper/pharm/data/<sub>/` |
| Design token / primitive / VM base | `:core:ui` | `app.devper.pharm.ui.<sub>` | `core/ui/src/commonMain/kotlin/app/devper/pharm/ui/<sub>/` |
| Expect/actual (any new platform-bound code) | `:core:common` | `app.devper.pharm.common[.<sub>]` | `core/common/src/<sourceSet>/kotlin/app/devper/pharm/common/.../` |
| Feature screen + VM + NavGraph | `:features` | `app.devper.pharm.presentation.<feature>` | `features/src/commonMain/kotlin/app/devper/pharm/presentation/<feature>/` |
| Feature DI module (ViewModels only) | `:features` | `app.devper.pharm.di` | `features/src/commonMain/kotlin/app/devper/pharm/di/<Feature>Module.kt` |
| Feature test | `:features` | `app.devper.pharm.presentation.<feature>` | `features/src/commonTest/kotlin/app/devper/pharm/presentation/<feature>/` |

## DI composition

Each layer owns its own Koin module — `:composeApp/di/AppModule.kt` is the composition root:

| Module | Where | What it binds |
|---|---|---|
| `commonModule` | `core/common/.../common/di/CommonModule.kt` | 1 binding only — `Logger` / `PrintlnLogger`. (`AppDispatchers` / `PdfDownloader` / `ReceiptPrinter` are now bound per-platform in `:composeApp`'s Main*.kt platform modules since the INVERT refactor.) |
| platform modules (×4) | `composeApp/<plat>Main/.../Main*.kt` | Per-platform `single<X> { … }` for `Settings` + `HttpClient(<engine>)` + `AppDispatchers(IO or Default)` + `PdfDownloaderImpl(<args>)` + `ReceiptPrinterImpl()` |
| `domainModule` | `core/domain/.../domain/di/DomainModule.kt` (composer) + 10 sibling `<Feature>DomainModule.kt` files | 77 bindings total: `StockChangeBus` + 5 `<X>Provider` singletons + `BulkImportJsonParser` + 70 use case factories — split across 10 per-feature DI files (`authDomainModule`, `customersDomainModule`, `salesDomainModule`, …) that `domainModule = module { includes(…) }` composes |
| `dataModule` | `core/data/.../data/di/DataModule.kt` | 40 bindings: `ApiConfig` + `AppJson` + `TokenStorage` / `ParkedCartStorage` / `OfflineSaleQueueImpl` + 15 `<X>Api` + 18 `<X>RepositoryImpl bind <X>Repository::class` |
| `<feature>Module` × 10 | `features/.../di/*.kt` | ViewModel factories **only** — every UC / Provider / Repo / Api is bound elsewhere |

```kotlin
val appModule = module {
    includes(
        commonModule,
        domainModule,
        dataModule,
        authModule, customersModule, suppliersModule,
        purchaseOrdersModule, kyModule,
        inventoryModule,
        salesModule,
        reportsModule,
        settingsModule,
        offlineSyncModule,
    )
    factoryOf(::AppViewModel)
}
```

Each platform's `Main*.kt` adds a `jvmPlatformModule` / `iosPlatformModule` / etc. that binds the right `HttpClient` engine + `Settings` impl, then `startKoin { modules(platformModule, appModule) }`.

## MVVM in presentation — strict rules

- **Composables only talk to ViewModels.** Never `koinInject()` a UseCase or Repository inside a `@Composable`. Even root coordinators like `AppNavHost` go through a VM ([`AppViewModel`](composeApp/src/commonMain/kotlin/app/devper/pharm/presentation/AppViewModel.kt)).
- Every VM extends `BaseViewModel<S>(initial)` from `:core:ui` (`app.devper.pharm.ui.common.BaseViewModel`). Internally it owns `_state: MutableStateFlow<S>` + helpers `setState { copy(...) }` and `launchResult(block = { useCase(...) }, onSuccess = { … }, onFailure = { … })`.
- Form VMs extend `BaseFormViewModel<S>` where `S : BaseFormUiState<S>` — gives F-bounded helpers `withSaving / withSaved / withError`.
- `*UiState` is an immutable `data class` — single source of truth per screen. Holds `loading: Boolean`, `error: String? = null`, plus screen-specific fields.
- ViewModels depend on **use cases** (and `Providers` for shared state observation), never on repositories, APIs, storage, or anything from `:core:data`.

## Error handling — single channel via `ErrorBottomSheet`

- VMs surface failures as `state.error: String?` (already a field on every `*UiState`); never throw from a use case path.
- Every screen renders [`ErrorBottomSheet(message = state.error, onDismiss = viewModel::dismissError)`](core/ui/src/commonMain/kotlin/app/devper/pharm/ui/components/ErrorBottomSheet.kt) at the bottom of its `@Composable` — the sheet is a no-op when `message == null`.
- VMs expose `fun dismissError() = setState { copy(error = null) }`.
- Typed exceptions from `:core:common.AppException` (`AuthException` / `NotFoundException` / `ConflictException` / `NetworkException` / `ServerException` / `ForbiddenException` …) are produced by `:core:data`'s Ktor `HttpResponseValidator`. `BaseUseCase` wraps execution in `runCatching` once at the boundary — UI layer never sees Throwable.

## Code style — NO COMMENTS in `.kt` files

Strict rule: **no comments anywhere in production or test Kotlin code**. No `//`, no `/* */`, no KDoc, no TODO/FIXME markers. Code must be self-documenting via names + types. If you feel the urge to comment, rename or refactor instead. Markdown and `build.gradle.kts` plugin metadata are exempt. Full rule + rationale in [`CLAUDE.md`](CLAUDE.md).

## Theme & fonts

- App uses **Sarabun** (Thai+Latin sans-serif from Cadson Demak, OFL) — same family as the React frontend.
- TTF files (Light/Regular/Medium/SemiBold/Bold) ship in [`core/ui/src/commonMain/composeResources/font/`](core/ui/src/commonMain/composeResources/font/).
- Compose Resources package is pinned to `app.devper.pharm.ui.resources` via `compose.resources { packageOfResClass = ... }` in [`core/ui/build.gradle.kts`](core/ui/build.gradle.kts).
- [`Typography.kt`](core/ui/src/commonMain/kotlin/app/devper/pharm/ui/theme/Typography.kt) builds a Material 3 `Typography()` swapping only the FontFamily.
- Design tokens (`PharmTokens`) + primitives live in `core/ui/.../ui/{theme,designsystem}/`:
  - **Buttons / inputs**: `PharmButton`, `PharmTextField`, `FormField`, `PharmToggleSwitch`
  - **Badges**: `PharmBadge`, `KyBadge`, `PharmStatusBadge` (semantic wrapper — `PharmStatus.Pending/Done/Voided/Active/Inactive/Vip/...`), `PharmAvatarCircle`
  - **Layout**: `PharmModal`, `PharmSidebar`, `PharmTopbar`, `PharmTabBar`, `PharmFilterChips` (multi-select) / `PharmSingleSelectChips`, `PharmDateRangeField` (M3 DatePicker-backed)
  - **Tables**: `PharmTable<T>(rows, columns, key, rowHeight, onRowClick, emptyContent, bottomRow)` with `PharmTableColumn(header, weight, align, cell)`; `PharmActionMenu(actions = [PharmAction(label, icon, tone)])`; `PharmStickyTotalRow` for bottom totals
  - **Cards**: `MetricCard`, `DrugCard`
  - **Charts**: `PharmMiniBarChart`, `PharmGroupedBarChart` (canvas-based)
  - **Icons**: `PharmIcons.*` — 32 SVG vector icons (stroke 1.75 / round caps / 24×24, design's exact paths via `PathParser`). Replaces emoji-as-icons in sidebar / topbar / actions.
- Design source: `เฮลท์ตี้ฟาร์ม` handoff bundle (HTML/CSS/JSX prototypes) — every `Pharm*` primitive maps 1:1 to a primitive in `ui_kits/pos/`.

## Form fields — pin height + skip the floating label

- **Always pin single-line `OutlinedTextField` / `TextField` to `Modifier.height(56.dp)`** (= Material 3's `OutlinedTextFieldDefaults.MinHeight`). Without this, the empty-vs-typed measure path produces a 1–2 px height variance per keystroke. In a centred parent (or any Column with sibling spacing), that variance amplifies into a visible "bounce" on the first character.
- **Avoid the `label = { … }` slot** when layout stability matters. Use the `FormField` pattern from `:core:ui.designsystem`: static `Text` label above the field plus a `placeholder` for hint copy.
- **Conditional `trailingIcon` must keep the slot reserved.** Always render the `IconButton`, gate visibility *inside* it (`if (cond) Icon(...)`) and use `enabled = cond`.

## Repository conventions

- **Methods with 2+ parameters group them into a `*Param` data class** in `core/domain/.../domain/param/<feature>/`. Example: `AuthRepository.login(param: LoginParam)`. Single-arg methods stay plain.
- The repository **implementation** (`:core:data`) maps the domain `*Param` → wire-shape `*Request` (in `data/remote/dto/`) via a `private fun *Param.toRequest()` extension at the bottom of the file. Domain never sees DTOs; data never leaks DTOs upward.
- The corresponding **API** class takes the DTO directly (`AuthApi.login(request: LoginRequest)`).
- **Phase Q**: repository impls drop `runCatching` — `BaseUseCase` wraps execution once. Two known exceptions: `runCatching { sales.serializeCheckout(...) }.getOrNull()` inside `CheckoutUseCase`, and per-row resilience inside `SubmitKyFormsUseCase`. Both live in `:core:domain/.../domain/usecase/` (not `:core:data`).

## DTO conventions — `@SerialName` on every field, camelCase Kotlin names

Every `@Serializable data class` property in `:core:data/.../data/remote/dto/` and `:core:data/.../data/storage/*Dto.kt` follows two rules:
1. **Kotlin field name is camelCase** (idiomatic Kotlin) — A25
2. **Every field carries an explicit `@SerialName("wire_name")`** — even when the wire name matches the Kotlin name — A24

Format is **single-line** for readability:

```kotlin
@Serializable
data class DrugDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("generic_name") val genericName: String? = null,
    @SerialName("sell_price") val sellPrice: Double = 0.0,
    @SerialName("cost_price") val costPrice: Double = 0.0,
    @SerialName("min_stock") val minStock: Int = 0,
    @SerialName("reg_no") val regNo: String? = null,
    @SerialName("alt_units") val altUnits: List<AltUnitDto>? = null,
    @SerialName("report_types") val reportTypes: List<String>? = null,
)
```

**Why both rules together**:
- Kotlin idiom is camelCase for properties — `dto.sellPrice` reads fluently in mapper code, `dto.sell_price` doesn't
- The wire shape (snake_case in the pharmacy backend's case) stays pinned via `@SerialName` — no implicit-default-matching surprises
- Renaming a Kotlin field can't silently change the over-the-wire contract; you have to also update `@SerialName`, and the breakage shows up at code-review time as obvious diff noise

**286 fields** across **17 DTO files** follow this convention today. New DTO fields breaking either rule are a P0 violation (A24 + A25 in `pharmacy-kmp-review` skill; both enforced at build time by `auditArchitecture` Gradle task — wire `@SerialName("snake_case")` strings are intentional and exempt).

## Backends used

Defaults point at the live Cloud Run hosts (asia-southeast1) so the app works without spinning anything up locally:

- `https://devper-um-1056670356976.asia-southeast1.run.app` — `um-api` (auth). The login screen POSTs `/api/um/v1/auth/login` with `{username, password, system: "PHARMACY"}` and stores the returned JWT.
- `https://pharmacy-api-1056670356976.asia-southeast1.run.app` — `pharmacy-app/backend`. Endpoints under `/api/pharmacy/v1/*`. Every request gets `Authorization: Bearer <token>` automatically; on 401 the token is cleared and the UI bounces back to login.

To point at a local backend, bind a different `ApiConfig` in the platform Koin module. Example for Android emulator:

```kotlin
single { ApiConfig(
    umBaseUrl  = "http://10.0.2.2:8585",
    apiBaseUrl = "http://10.0.2.2:8087",
) }
```

Cleartext (http://) is **disabled** by default on Android (no `usesCleartextTraffic`) and iOS (no ATS exemption). If you switch to a localhost override, add a `network_security_config.xml` allowing cleartext for `10.0.2.2` / `localhost` and re-add the ATS exemption to `Info.plist`.

## CI

Every push to `main` and every PR runs [`.github/workflows/check.yml`](.github/workflows/check.yml) on `macos-latest` (required for the iOS targets). The workflow runs the same checks as the local verify command:

1. `:composeApp:auditArchitecture` — fails on any A10/A17/A19/A20/A23/A24/A25/A26/A27/A28 violation
2. JVM tests across all 6 modules (373 unique tests)
3. `:composeApp:testDebugUnitTest` — Android debug variant
4. `:composeApp:compileTestKotlinIosSimulatorArm64` — iOS Simulator (Arm64) compile-only check
5. `:composeApp:compileTestKotlinWasmJs` — Web (wasmJs) compile-only check

Caches `~/.gradle` (auto via `gradle/actions/setup-gradle@v4`) and `~/.konan` (manual via `actions/cache@v4` keyed on `libs.versions.toml` + `build.gradle.kts` hashes) so warm runs finish in ~3–5 min vs ~15 min cold. On failure, the workflow uploads `**/build/reports/tests/`, `architecture-audit.txt`, and `**/build/test-results/` as artifacts (7-day retention).

## Run

```bash
cd /Users/admin/ProjectPos/pharmacy-app/app-kmp

# Full check — runs all JVM tests + Android debug unit tests + lint + auditArchitecture
./gradlew :composeApp:check

# Or pick targets explicitly (373 unique tests + compile for iOS sim + wasm)
./gradlew :composeApp:testDebugUnitTest \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs \
          :features:jvmTest :core:domain:jvmTest \
          :core:common:jvmTest :core:ui:jvmTest :core:data:jvmTest

# Architecture audit on its own (greps for stale inward-only violations)
./gradlew :composeApp:auditArchitecture

# Run a target
./gradlew :composeApp:installDebug                   # Android (device or emulator)
./gradlew :composeApp:run                            # Desktop (JVM)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun    # Web (opens in browser)
./gradlew :composeApp:iosSimulatorArm64Test          # iOS framework smoke check
```

For iOS as a real app: open `iosApp/iosApp.xcodeproj` in Xcode. `iosApp.swift` + `Info.plist` are the source files; add them to a fresh "App" project, link `composeApp`'s framework via the Run Script phase that the Compose docs describe.

## Adding a new feature

For a new feature called `<X>` (e.g. `loyalty`):

1. **Domain** (in `:core:domain`)
   - `domain/model/<X>.kt` — pure data class
   - `domain/param/<feature>/<X>Param.kt` *(only if any operation takes 2+ inputs)*
   - `domain/repository/<feature>/<X>Repository.kt` — interface (`suspend fun list(): Result<List<X>>`, etc.)
   - `domain/usecase/<feature>/Get<X>sUseCase.kt`, etc. — each `class FooUseCase(dispatchers: AppDispatchers, …) : BaseUseCase<P, R>(dispatchers)`
   - **Add bindings to `domain/di/<Feature>DomainModule.kt`**: `factoryOf(::Get<X>sUseCase)`, etc. (if it's a new feature, create the file + add to `DomainModule.kt`'s `includes(...)`)
2. **Data** (in `:core:data`)
   - `data/remote/dto/<X>Dto.kt`, `data/remote/dto/Add<X>Request.kt` — `@Serializable`
   - `data/remote/api/<X>Api.kt` — Ktor calls
   - `data/repository/<X>RepositoryImpl.kt` — implements `<X>Repository`; private `*Param.toRequest()` extension at bottom
   - **Add bindings to `data/di/DataModule.kt`**: `singleOf(::<X>Api)` + `singleOf(::<X>RepositoryImpl) bind <X>Repository::class`
3. **Presentation** (in `:features`)
   - `presentation/<feature>/<X>ListScreen.kt` + `<X>ListViewModel.kt` + `<X>ListUiState.kt`
   - `presentation/<feature>/<Feature>NavGraph.kt` + `<Feature>Routes.kt` (`@Serializable` route classes)
   - **Add bindings to `di/<Feature>Module.kt`**: `factoryOf(::<X>ListViewModel)` (VMs only!)
4. **Wire from `:composeApp`**
   - Add `<feature>Module` to `appModule.includes(...)` in [`AppModule.kt`](composeApp/src/commonMain/kotlin/app/devper/pharm/di/AppModule.kt)
   - Add `<feature>Graph(navController, onLogout, pendingSyncCount)` to [`AppNavHost.kt`](composeApp/src/commonMain/kotlin/app/devper/pharm/presentation/navigation/AppNavHost.kt)
5. **Test** (in `:features/commonTest`)
   - `presentation/<feature>/<X>ViewModelTest.kt` — uses `runVmTest { dispatchers -> }` helper from `:core:ui` + a `Fake<X>Repository` in `features/.../fakes/`

The `pharmacy-kmp-feature` skill ([repo skills dir](.claude/skills/pharmacy-kmp-feature/)) automates most of this.

## Skills + Agents

In-repo skills live under `.claude/skills/` (mirrored to `~/.agents/skills/`):

| Skill | What it does |
|---|---|
| `pharmacy-kmp-feature` | Scaffold a new end-to-end feature (domain → data → presentation → DI → nav → test) |
| `pharmacy-kmp-add-form` | Add a new `BaseFormViewModel`-style form with validation |
| `pharmacy-kmp-test` | Write a VM test using `runVmTest` + `Fake<X>Repository` pattern. Enforces **minimum coverage rule**: every feature ships with `<X>ViewModelTest.kt` per VM + unit tests for non-trivial use cases |
| `pharmacy-kmp-screen-split` | Refactor a fat Screen into `Screen` ↔ `Content` + `@Preview` variants |
| `pharmacy-kmp-review` | Audit a change against M → CC architectural rules + 28 numbered P0 checks (A1–A28) + 32 P1 convention checks (C1–C32) + 8 P2 polish (Q1–Q8) |
| `compose-multiplatform-patterns` | General Compose Multiplatform patterns (state, navigation, slot APIs, performance, theming) + project-specific deviations table |
| `kotlin-coding-style` | Idiomatic Kotlin (val/var, sealed types, null safety, scope functions, error handling) + pharmacy deviations (no comments, no `Result<T>` in repos, typed `AppException`) |
| `git-workflow` | Branching, conventional commits, merge vs rebase, PRs + Claude Code safety rails (no amend, no force-push, no `--no-verify`, `Co-Authored-By` trailer) |
| `update-docs` | Keep `README.md` in sync after feature / API / model / dep changes |

Agents (live under `.claude/agents/`):

| Agent | What it does |
|---|---|
| `kotlin-reviewer` | Senior Kotlin/Android/KMP code reviewer — reads `git diff`, applies the 28 architecture checks + Compose / coroutine / lifecycle traps, outputs `[CRITICAL]/[HIGH]/[MEDIUM]/[LOW]` report. Recognises project-specific "looks-wrong-but-is-right" patterns (split-package `BaseUseCase`, bare `T` repo returns, no VM dispatchers, etc.) |

## Tests

**373 unique tests across 6 modules** (~540 on a full `./gradlew :composeApp:check` once you double-count Android variant runs):

| Module | jvmTest count | What's covered |
|---|---|---|
| `:core:common` | 16 | `AppException` typed errors, `Logger`/`PrintlnLogger`, `BaseUseCase`/`BaseSyncUseCase` success+failure paths |
| `:core:domain` | 71 | model invariants, parsers (`BulkImportJsonParser`, `PurchaseOrderInputBuilder`, etc.), util (`BarcodeMatcher`, `DrugSearch`, `UmRoleValidator` — 4×4 actor×target + isSelf override, …), pricing (`resolvePrice` tier resolution) |
| `:core:ui` | 33 | `formatBaht` / `formatBahtCurrency` / `fmtBaht` rounding + thousands separators; `BaseViewModel` setState + launchResult; `BaseFormViewModel` saving / saved / error transitions; new design-system primitive logic — `PharmAvatarCircle.initialsFrom()` (5 tests), `PharmStatusBadge` tone mapping (6 tests) |
| `:core:data` | 20 | `AppJson` lenient/strict; `ApiConfig` URL helpers; Ktor `HttpResponseValidator` HTTP-status → typed `AppException` translation (via `MockEngine`); `OfflineSaleQueueImpl` FIFO + persistence round-trip |
| `:features` | 241 | ViewModel unit tests across 40+ files via `runVmTest { dispatchers -> }` helper + `Fake<X>Repository` pattern (incl. `ProfileViewModelTest`, `UsersListViewModelTest`, `UserFormViewModelTest`, `SettingsEditorViewModelTest`, `SettingsMenuRegistryTest`) |
| `:composeApp` | 1 | `AppModuleWiringTest` — boots Koin with `commonModule + domainModule + dataModule + 10 feature modules + test platform module` and resolves every `<X>ViewModel` (38 in total) to catch "forgot a `factoryOf(...)` binding" regressions before runtime |

Build-time **architecture audit** (in `build-logic/.../pharmacy.architecture.audit.gradle.kts`) runs as part of `:composeApp:check` and fails the build on any stale import that would violate the inward-only rules (A10/A17/A19/A20/A23/A24/A25/A26/A27/A28 — 10 enforced at build time of the 28 total in `pharmacy-kmp-review` skill).
- Verify command above runs them all on JVM + compiles tests for iOS sim + wasmJs

## Out of scope

- Cart / sale checkout, customers, suppliers, KY forms, reports, …  — **all implemented**. See `features/src/commonMain/kotlin/app/devper/pharm/presentation/<feature>/`.
- Offline queue + auto-sync — implemented (`:features/presentation/offlinesync/`).
- Barcode scanner — implemented as `BarcodeScannerModifier` HID listener in `:core:ui/scanner/`.
- AGP 9 migration — deferred (currently AGP 8.13.2).
- iOS Framework split per-feature — single `ComposeApp.framework` built by `:composeApp`.
- Per-feature module split (`:features:<X>`) — folder structure inside `:features` is ready for it, just hasn't been done yet.

## More

- [`MODULE_GRAPH.md`](MODULE_GRAPH.md) — full module dep matrix + "what lives where" tables
- [`CLAUDE.md`](CLAUDE.md) — project-scoped conventions (no-comments rule, module layout, verify command)
- [`/Users/admin/ProjectPos/CLAUDE.md`](../../CLAUDE.md) — workspace-wide conventions (auth flow, roles, Thai-first copy)
