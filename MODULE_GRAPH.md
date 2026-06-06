# Module graph — pharmacy-app KMP companion

**26-module Gradle layout**. Dependency direction flows **inward only**:
from entry point `:composeApp` down through 20 `:features:<x>` →
`:core:{ui,data}` → `:core:domain` → `:core:common`,
with `:core:common` at the bottom depending on nothing else in the
project. A test-only side module `:features:test-fixtures` hosts shared
`Fake*Repository` classes consumed by feature `commonTest` source sets.
The app shell + navigation live entirely in `:composeApp` (`MainShell` +
`AppNavHost` + `MainNav.kt`); the former `:features:shared` module was removed.

## Graph

```
                            ┌────────────────┐
                            │  :composeApp   │   entry point (only module
                            │  (app shell)   │   with platform source folders)
                            └────────┬───────┘   App / AppViewModel / AppNavHost
                                     │
        ┌────────────────────────────┼────────────────────────────┐
        │              20 :features:<x> modules                   │
        │  auth bulkimport customers expiry help imports ky       │
        │  labels movements offlinesync planning profile reports  │
        │  saleshistory sell settings stock stockcount suppliers  │
        │  users                                                  │
        └────────────────────────────┬────────────────────────────┘
                                     │       (:features:test-fixtures sits
                                     │        beside :features:<x> as a
                                     │        commonTest-only dependency of
                                     │        each :features:<x> module)
              ┌────────────────────┼────────────────────┐
              ▼                    ▼                    ▼
       ┌────────────┐       ┌────────────┐       ┌────────────┐
       │ :core:ui   │       │ :core:data │       │ :core:common│
       │ theme +    │       │ repo impls │       │ via         │
       │ DS + base  │       │ + transport│       │ :core:domain│
       └─────┬──────┘       └─────┬──────┘       │  api()      │
             │                    │              └─────────────┘
             └──────────┬─────────┘
                        ▼
                ┌────────────────┐
                │  :core:domain  │  pure kotlin
                │  (commonMain + │  models / usecases /
                │   commonTest)  │  repos (interfaces)
                └────────┬───────┘
                         ▼
                ┌────────────────┐
                │  :core:common  │  interfaces + pure infra
                │  (no project   │  AppDispatchers / Logger / AppException
                │   deps;        │  FileDownloader interface
                │   commonMain   │  ReceiptPrinter interface
                │   only)        │  BaseUseCase / BaseSyncUseCase
                └────────────────┘
   ↑ impls of FileDownloader / ReceiptPrinter live in :composeApp/<plat>Main
```

## Dependency matrix

| Module                       | depends on                                                       |
|------------------------------|------------------------------------------------------------------|
| `:composeApp`                | `:core:{common,domain,ui,data}` + all 20 `:features:<x>` |
| `:features:<x>` (20 modules) | `:core:domain` + `:core:ui` (+ kotlinx-datetime where needed) |
| `:features:<x>` `commonTest` | `:features:test-fixtures` (test-only dep for the features that use shared fakes; help/saleshistory/auth/bulkimport/reports/stockcount don't need it because their tests use their own co-located fakes or are pure-logic) |
| `:features:test-fixtures`    | `:core:common` + `:core:domain` (only) — test-only; commonMain is fakes-as-production-code |
| `:core:ui`                   | `:core:domain`, `:core:common` (+ compose)                       |
| `:core:data`                 | `:core:domain`, `:core:common` (+ ktor + multiplatform-settings + koin-core) |
| `:core:domain`               | `api(:core:common)` (re-exported so consumers reach `AppDispatchers`/`AppException`/`BaseUseCase` etc. transitively) (+ kotlinx) |
| `:core:common`               | _none_ (kotlinx-only — host of every cross-platform interface) |

**Per-feature deps are now P0 compile errors.** Before the split,
cross-feature imports inside `:features` (e.g.
`presentation.sell → presentation.customers`) were only a P1 audit
warning. After the split, the Kotlin compiler refuses any foreign-module
symbol without an explicit `project(":features:<x>")` dep — the audit
check became redundant for cross-feature rules (it still enforces
A26/A27/A28 for platform folders, expect declarations, generic
exceptions).

**`:features` decoupling**: no per-feature module can reach `:core:data`
(no direct dep, no transitive path — `:core:domain` does not re-export
`:core:data`). Repository bindings (`<X>RepositoryImpl bind
<X>Repository::class` + `<X>Api`) live in `:core:data`'s own Koin
module (`dataModule` at `core/data/.../data/di/DataModule.kt`), which
`:composeApp` includes via `appModule.includes(dataModule, …)`.
Features inject `<X>Repository` interfaces from `:core:domain` — they
never name a concrete `RepositoryImpl`.

`:features:<x>` CAN still reach `:core:common` symbols (e.g.
`AppDispatchers` in VM tests, `ReceiptPrinter` in `CheckoutViewModel`)
via `:core:domain`'s `api()` re-export. This is intentional — the dep
direction is correct (features → domain → common), just declared once
at the layer that needs to re-export.

**Note on DI module layout**:
- `:core:domain` → `domainModule` (76 bindings: 5 providers + 1 parser + 68 use cases + 2 settings use cases)
- `:core:data` → `dataModule` (33 bindings: Apis + RepositoryImpls)
- `:features:<x>/di/<Feature>Module.kt` → ONLY VM `factoryOf` bindings (one DI module per feature module)
- `:composeApp/di/AppModule.kt` → composition root: `includes(commonModule, domainModule, dataModule, authModule, customersModule, …, sellModule, settingsModule, …)` — 20 per-feature includes.

## What lives where

### `:composeApp` — app shell only (only module with platform source folders)

After the INVERT refactor, `:composeApp` is **the** only module in the
project that has `androidMain` / `iosMain` / `jvmMain` / `wasmJsMain`
source sets. It owns:
- The Compose Application entry points (Main*.kt)
- Every platform impl of cross-cutting interfaces (FileDownloaderImpl, ReceiptPrinterImpl)
- Per-platform Koin module bindings (Settings, HttpClient engine, AppDispatchers, FileDownloader, ReceiptPrinter)

| Path | Contents |
|---|---|
| `commonMain/.../App.kt` | Root composable |
| `commonMain/.../presentation/AppViewModel.kt`, `AppUiState.kt` | Global VM (login state + offline queue badge + UiPreferences) |
| `commonMain/.../presentation/navigation/AppNavHost.kt` | Wires all 20 feature `*Graph` extensions |
| `commonMain/.../di/AppModule.kt` | `includes(commonModule, domainModule, dataModule, + 20 feature modules)` + binds `AppViewModel` |
| `androidMain/.../MainActivity.kt`, `PharmacyApplication.kt` | Android entry; `androidPlatformModule` binds `Settings` + `HttpClient(OkHttp)` + `AppDispatchers(IO)` + `FileDownloaderImpl(context)` + `ReceiptPrinterImpl()` |
| `androidMain/.../platform/{FileDownloaderImpl,ReceiptPrinterImpl}.kt` | Android impls: MediaStore.Downloads for API 29+, legacy Environment for older |
| `iosMain/.../MainViewController.kt` | iOS entry; `iosPlatformModule` mirrors Android with Darwin engine |
| `iosMain/.../platform/{FileDownloaderImpl,ReceiptPrinterImpl}.kt` | iOS impl: `UIActivityViewController` share sheet for files; ReceiptPrinter still a logging stub |
| `jvmMain/.../Main.kt` | Desktop entry; `jvmPlatformModule` with Java engine + real `Dispatchers.IO` + Desktop printer + ~/Downloads file saver |
| `jvmMain/.../platform/{FileDownloaderImpl,ReceiptPrinterImpl}.kt` | JVM impls — `java.awt.print.PrinterJob` receipt + writes to `~/Downloads` + opens via `Desktop` |
| `wasmJsMain/.../Main.kt` | Web entry; `webPlatformModule` with JS engine |
| `wasmJsMain/.../platform/{FileDownloaderImpl,ReceiptPrinterImpl}.kt` | wasmJs impls — base64 anchor download + iframe `window.print()` for receipts |

All platform impls live under one package per platform:
`app.devper.pharm.platform.{FileDownloaderImpl,ReceiptPrinterImpl}` —
single per-platform "platform-impl" folder.

### `:core:common` — pure cross-cutting infra (commonMain + commonTest only, zero project deps)

After the INVERT refactor, `:core:common` has **no platform source
folders** at all. Every platform-bound concern is abstracted as an
`interface` here; impls live in `:composeApp/{platform}Main/` and are
bound via Koin in each Main*.kt's platform module.

| Path | Package | Contents |
|---|---|---|
| `common/AppDispatchers.kt` | `app.devper.pharm.common` | `data class AppDispatchers(main, io, default)` — constructed per-platform |
| `common/Logger.kt` | `app.devper.pharm.common` | `Logger` interface + `PrintlnLogger` impl |
| `common/AppException.kt` | `app.devper.pharm.common` | Sealed `AppException` + 9 typed subclasses (Auth, Forbidden, NotFound, Conflict, Network, Server, Validation, Storage, UnsupportedPlatform) — domain error language; enforced by A28 |
| `domain/usecase/BaseUseCase.kt` | `app.devper.pharm.domain.usecase` | `BaseUseCase<P,R>` + `BaseSyncUseCase<P,R>` framework |
| `common/print/{ReceiptTemplate,ReceiptPrinter}.kt` | `app.devper.pharm.common.print` | `ReceiptTemplate` data + `ReceiptPrinter` interface |
| `common/platform/FileDownloader.kt` | `app.devper.pharm.common.platform` | **interface** `FileDownloader { suspend fun save(filename, mimeType, bytes): Result<String> }` + `MimeType` constants |
| `common/di/CommonModule.kt` | `app.devper.pharm.common.di` | Koin module — binds `Logger` only |

### `:core:domain` — pure domain (commonMain + commonTest only)

| Path | Contents |
|---|---|
| `model/` | Drug, Customer, Supplier, Sale, Cart*, Lot, Adjustment, StockCount, PurchaseOrder, KyEntry*, KyForms, Settings, User, Role, LabelPrint, UiPreferences, … |
| `param/<feature>/` | All `*Param` inputs grouped per feature |
| `repository/<feature>/` | Repo interfaces grouped per feature (impls live in `:core:data`) |
| `usecase/<feature>/` | 70+ use cases grouped per feature: auth, customers, suppliers, inventory, ky, offlinesync, purchasing, reports, sales, settings, labels. Each extends `BaseUseCase` / `BaseSyncUseCase`. |
| `parser/`, `util/`, `pricing/`, `event/`, `observer/` | Cross-cutting domain helpers + 5 Provider classes |
| `di/<Feature>DomainModule.kt` × 10 | Per-feature Koin modules. Composer `DomainModule.kt` includes all. |

### `:core:ui` — shared compose infra (commonMain + commonTest only)

| Path | Contents |
|---|---|
| `ui/theme/` | Color, Theme, Typography, DesignTokens (PharmTokens with `fontScale`) |
| `ui/designsystem/` | `PharmButton`, `PharmBadge`, `PharmTextField`, `PharmModal`, `PharmTopbar`, `PharmSidebar`, `MetricCard`, `DrugCard`, `FormField`, `KyBadge`, `PharmTable`, `PharmFilterChips`, `PharmActionMenu`, `PharmIcons` (32 SVG vectors) |
| `ui/common/` | `BaseUiState`, `BaseViewModel`, `BaseFormViewModel`, `BaseFormUiState`, `RunVmTest` |
| `ui/components/` | `AppShell`, `ErrorBottomSheet`, `WindowSize` |
| `ui/format/` | `Money.kt` (formatBaht / formatBahtCurrency / fmtBaht) |
| `ui/scanner/` | `BarcodeScannerModifier` (HID listener) |
| `ui/print/` | `ReceiptBuilder` (pure — `ReceiptTemplate` lives in `:core:common`) |
| `ui/help/MarkdownText.kt` | Markdown renderer (used by `:features:help`) |
| `composeResources/font/sarabun_*.ttf` | 5 weights |

`compose.resources { packageOfResClass = "app.devper.pharm.ui.resources" }`

### `:core:data` — repository impls + transport (commonMain + commonTest only)

The ktor engines (`ktor-client-okhttp`/`darwin`/`java`/`js`) live in
`:composeApp`'s per-platform source sets, not here.

| Path | Contents |
|---|---|
| `data/network/` | `HttpClient` builder, `AppJson`, `ApiConfig`, `HttpResponseValidator` |
| `data/storage/` | `TokenStorage`, `ParkedCartStorage`, `OfflineSaleQueueImpl` (multiplatform-settings adapters) |
| `data/repository/` | All `*RepositoryImpl` (Phase Q — no `runCatching` here) + `UiPreferencesRepositoryImpl` + `LabelRepositoryImpl` + `ExportRepositoryImpl` |
| `data/remote/api/` | All `*Api` interfaces + endpoint paths |
| `data/remote/dto/` | DTOs + Request/Response types. A24 + A25 enforced (camelCase + `@SerialName`) |
| `data/di/DataModule.kt` | Koin `dataModule` — every `<X>Api` + every `<X>RepositoryImpl bind <X>Repository::class` |

### Navigation lives in `:composeApp` (single shell, two-level NavHost)

There is no `:features:shared` module. The app shell + navigation are owned by
`:composeApp`:

| Path | Contents |
|---|---|
| `presentation/navigation/AppNavHost.kt` | Outer `NavHost(startDestination = Login)` with two destinations: `authNav` (Login, no shell) and `composable<MainRoot> { MainShell(...) }`. `LaunchedEffect(isLoggedIn)` swaps Login↔MainRoot. |
| `presentation/navigation/MainNav.kt` | `MainRoot` route, `MAIN_NAV_TABLE` (sidebar items), `DEST_INFO` map (`route qualified name → title + sectionKey`), and `MainShell` — renders `AppShell` ONCE around a nested `NavHost(startDestination = Sell)` that composes all 20 `<x>Nav` builders. Active item + title derive from `nestedNav.currentBackStackEntryAsState()`. |

Each feature owns its routes + nav builder at
`features/<x>/.../presentation/<x>/navigation/<X>Nav.kt` (`@Serializable` route
objects + `fun NavGraphBuilder.<x>Nav(...)`, no shell). Cross-feature navigation
is done via hoisted `() -> Unit` callbacks resolved in `MainShell` (e.g.
`auth→Sell`, `stock→ReorderSuggestions`), not by importing another feature's route.

### `:features:test-fixtures` — shared test doubles (commonMain only)

Test-only Kotlin module exposing 14 `Fake*Repository` classes as
`commonMain` source so any feature's `commonTest` can depend on the
module and import the fakes.

| Path | Contents |
|---|---|
| `domain/repository/Fake{Cart,Customer,Drug,Ky,Label,OfflineSaleQueue,Profile,PurchaseOrder,Reports,Sale,Settings,StockCounts,Supplier,UiPreferences,Users}Repository.kt` | 15 fakes shared by ≥2 feature modules' tests |

`deps`: `:core:common` + `:core:domain` + kotlinx-coroutines-core only.

**A28 audit exclusion**: `pharmacy.architecture.audit.gradle.kts` skips
`/features/test-fixtures/` for the generic-exception check. The fakes
throw `RuntimeException("...")` deliberately as a test signal; typing
them as `AppException` subclasses would be ceremony for no value.

### `:features:<x>` — 20 per-feature modules

| Module | Files (prod / test) | Notable |
|---|---|---|
| `:features:auth` | 4 prod + 6 tests + 1 co-located fake | Login screen; navigates to Sell via shared route on success |
| `:features:bulkimport` | 9 prod + 8 tests + 1 co-located fake | Drag-drop area + JSON import wizard |
| `:features:customers` | 14 prod + 2 tests | List + Form + Detail + form/ subdir |
| `:features:expiry` | 8 prod | Lot expiry tracker with bulk write-off |
| `:features:help` | 6 prod + 9 tests + 1 markdown asset | `composeResources/files/user_guide.md`; own `packageOfResClass = "app.devper.pharm.features.help.resources"` |
| `:features:imports` | 21 prod + 1 test | Purchase orders — biggest after sell; includes drug + supplier picker dialogs |
| `:features:ky` | 13 prod | KHY9 + KyList for forms 10/11/12 |
| `:features:labels` | 6 prod + 16 tests | Code128 barcode label printing (backend ships Code128 PDFs) |
| `:features:movements` | 11 prod | Stock movements log with type-filter chips |
| `:features:offlinesync` | 8 prod + 4 tests | Offline sale queue monitor with metrics + retry/cancel |
| `:features:planning` | 13 prod | LowStock list + ReorderSuggestions, bundled because they share domain semantics |
| `:features:profile` | 6 prod + 11 tests | Theme/font-scale switcher wired into running app |
| `:features:reports` | 28 prod + 15 tests | Reports dashboard + Profit + Eod, biggest reports-cluster module |
| `:features:saleshistory` | 11 prod + 13 tests + 1 co-located fake | List + return-sale sheet |
| `:features:sell` | 39 prod + 6 tests | Heaviest feature — Sell + 5 sibling VMs (Checkout, DrugPicker, CustomerPicker, ParkedCart, VoidSale) + 23 component sheets |
| `:features:settings` | 15 prod + 7 tests | 5-tab editor (Store / Receipt / Pharmacist / Stock / Ky) + admin links menu |
| `:features:stock` | 23 prod + 30 tests + 2 co-located fakes | Stock list + DrugForm + DrugLots + StockAdjustments siblings |
| `:features:stockcount` | 16 prod + 7 tests | Physical-count list + Form sibling |
| `:features:suppliers` | 11 prod + 1 test | List + Form with form/ subdir |
| `:features:users` | 12 prod + 2 tests | List + Form |

Each `:features:<x>` follows the same pattern:
- `build.gradle.kts` applies `pharmacy.kmp.compose.library`, depends on
  `:core:domain` + `:core:ui` (+ kotlinx-datetime when the feature touches dates)
- `commonMain/kotlin/app/devper/pharm/di/<Feature>Module.kt` — ONLY VM
  `factoryOf` bindings
- `commonMain/kotlin/app/devper/pharm/presentation/<feature>/` — Screen,
  Content, Callbacks, ViewModel, UiState + section files +
  sometimes `components/`, `internal/`, `form/` sub-folders, and
  `navigation/<Feature>Nav.kt` (routes + `fun NavGraphBuilder.<x>Nav(...)`, no shell)
- `commonTest/kotlin/app/devper/pharm/presentation/<feature>/` (when tests
  exist) — VM tests + locally-scoped fakes
- `composeResources/` (rare — only `:features:help` ships one today)
- `packageOfResClass = "app.devper.pharm.features.<x>.resources"` if
  composeResources is enabled

## Forbidden imports (P0)

Audited at build time by the `auditArchitecture` Gradle task
(in `build-logic/.../pharmacy.architecture.audit.gradle.kts`). Runs as
part of `:composeApp:check` and on every PR. Kotlin's module system
enforces cross-feature boundaries directly — the audit no longer needs
to check them.

| From            | To                | Status |
|-----------------|-------------------|--------|
| `:core:common`  | any project module | ❌ P0 |
| `:core:domain`  | `:core:ui` / `:core:data` / `:features:*` / `:composeApp` | ❌ P0 |
| `:core:ui` / `:core:data` | `:features:*` | ❌ P0 |
| `:core:*`       | `:composeApp`     | ❌ P0 |
| `:features:<x>` | `:core:data` (any class — even via DI) | ❌ P0 (use `:core:domain` repo interfaces) |
| `:features:<x>` | `:composeApp` | ❌ P0 |
| `:features:<x>` | `:features:<y>` (different feature, production code) | ❌ P0 **Kotlin compile error** (was P1 before split arc; the audit check became redundant) |
| Any module other than `:composeApp` | `androidMain` / `iosMain` / `jvmMain` / `wasmJsMain` source folder | ❌ A26 |
| Anywhere in the repo | `expect class` / `expect fun` / `expect val` declarations | ❌ A27 |
| Production code | `throw IllegalStateException` / `RuntimeException` / etc. | ❌ A28 (excludes `:features:test-fixtures/`) |

## Test layout

| Module                       | Test source set | Tests in suite |
|------------------------------|-----------------|----:|
| `:core:common`               | `commonTest`    | 16 |
| `:core:domain`               | `commonTest`    | 88 |
| `:core:ui`                   | `commonTest`    | 62 (incl. MoneyFormatTest) |
| `:core:data`                 | `commonTest`    | 41 (incl. UiPreferencesRepositoryImpl + CartRepositoryImpl atomicity) |
| `:features:test-fixtures`    | none (fakes are commonMain) | 0 |
| 20× `:features:<x>`          | `commonTest`    | 305 across 29 module test suites |
| `:composeApp`                | `commonTest`    | 1 (`AppModuleWiringTest` — resolves every VM via Koin) |

**Project-wide**: 513 `@Test` functions on JVM. The Android
`testDebugUnitTest` target runs the same `commonTest` sources separately,
so the doubled count is higher on a full `:composeApp:check`.

Run everything:
```bash
./gradlew :composeApp:auditArchitecture \
          :composeApp:testDebugUnitTest \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs \
          :features:auth:jvmTest :features:bulkimport:jvmTest \
          :features:customers:jvmTest :features:expiry:jvmTest \
          :features:help:jvmTest :features:imports:jvmTest \
          :features:ky:jvmTest :features:labels:jvmTest \
          :features:movements:jvmTest :features:offlinesync:jvmTest \
          :features:planning:jvmTest :features:profile:jvmTest \
          :features:reports:jvmTest :features:saleshistory:jvmTest \
          :features:sell:jvmTest :features:settings:jvmTest \
          :features:stock:jvmTest :features:stockcount:jvmTest \
          :features:suppliers:jvmTest :features:users:jvmTest \
          :core:common:jvmTest :core:domain:jvmTest \
          :core:ui:jvmTest :core:data:jvmTest
```

Quick smoke (no per-feature breakdown — runs the dependent tree):
```bash
./gradlew :composeApp:check
```

## Adding new code — quick lookup

| You want to add… | Goes in… |
|---|---|
| A new cross-platform interface (dispatcher / logger / file IO / etc.) | `:core:common` |
| Its per-platform impl | `:composeApp/<plat>Main/platform/` + bind in that platform's Koin module |
| A new domain model / use case | `:core:domain` |
| A new repository interface | `:core:domain/repository/<feature>/` |
| The impl for that interface | `:core:data/repository/` + bind in `:core:data/di/DataModule.kt` |
| A new DTO / API endpoint | `:core:data/remote/{dto,api}/` |
| A new design primitive | `:core:ui/designsystem/` |
| Anything theme / color / token | `:core:ui/theme/` |
| A new feature route + nav builder | `:features:<feature>/presentation/<feature>/navigation/<Feature>Nav.kt` (routes + `fun NavGraphBuilder.<x>Nav(...)`, no shell) |
| A new sidebar entry + topbar title for a route | `composeApp/.../navigation/MainNav.kt` (`MAIN_NAV_TABLE` + `DEST_INFO`) |
| A new feature screen + VM | `:features:<feature>/presentation/<feature>/` |
| Its DI bindings | `:features:<feature>/di/<Feature>Module.kt` |
| A new VM test | `:features:<feature>/commonTest/.../<feature>/` |
| A test double used by only one feature | Co-locate in that feature's `commonTest/.../fakes/` |
| A test double used by ≥2 features | `:features:test-fixtures/commonMain/.../repository/` |
| Wiring a new feature into nav | `composeApp/.../navigation/MainNav.kt` (call `<x>Nav(nestedNav)` in `MainShell`) |
| Wiring a new feature into DI | `composeApp/.../di/AppModule.kt` (`includes(…)`) |

## Per-feature recipe

For a brand-new feature (also documented in CLAUDE.md):

1. `mkdir -p features/<feat>/src/commonMain/kotlin/app/devper/pharm/presentation/<feat>/`
2. Add `features/<feat>/build.gradle.kts` mirroring an existing leaf
   feature (`:features:help` is the smallest template)
3. Add `:features:<feat>` to `settings.gradle.kts`
4. Add `navigation/<Feat>Nav.kt` to the feature — `@Serializable` route objects +
   `fun NavGraphBuilder.<feat>Nav(navController)` declaring `composable<Route>`
   destinations directly (NO `ShelledScreen`); package stays
   `app.devper.pharm.presentation.<feat>`
5. Create `features/<feat>/.../presentation/<feat>/<Feat>{Screen,Content,
   Callbacks,ViewModel,UiState}.kt` + `features/<feat>/.../di/<Feat>Module.kt`
6. Wire from `:composeApp`: add `implementation(project(":features:<feat>"))`
   to `composeApp/build.gradle.kts`, call `<feat>Nav(nestedNav)` inside `MainShell`'s
   nested `NavHost` + add `DEST_INFO` entries (and a `MAIN_NAV_TABLE` row if it gets a
   sidebar item) in `composeApp/.../navigation/MainNav.kt`, and append `<feat>Module`
   to `AppModule.kt`'s `includes(...)`.

## Convention plugin (`build-logic/`)

Build-script boilerplate is centralized in an included build:

```
build-logic/
├── settings.gradle.kts          (rootProject + version catalog re-exposed)
├── build.gradle.kts             (kotlin-dsl + AGP + Kotlin + Compose Gradle plugins on classpath)
└── src/main/kotlin/
    ├── pharmacy.kmp.library.gradle.kts          (base KMP library — 5 targets + JDK 17 + namespace)
    ├── pharmacy.kmp.compose.library.gradle.kts  (extends base with compose plugins + common compose deps + compose.resources defaults)
    └── pharmacy.architecture.audit.gradle.kts   (auditArchitecture task — A10/A17/A19/A20/A23/A24/A25/A26/A27/A28)
```

`:composeApp` is **not** on the convention plugins — it's an Android
Application with its own structure (compose desktop block, wasmJs
executable, per-platform Main*.kt + per-platform Koin module). It does
apply `pharmacy.architecture.audit` to host the audit task.

Each library module's `build.gradle.kts` collapses dramatically —
the smallest feature modules (`:features:help`, `:features:profile`,
etc.) are 18-22 lines.

## Migration history

- **Phase S**: extract `:domain` from monolithic `:composeApp`
- **MM-1**: rename `:domain` → `:core:domain`; create `:core:ui` + `:core:data`
- **MM-2**: create `:features` and move all feature folders + DI + tests
- **MM-3**: slim `:composeApp` to entry-only
- **MN**: extract `:core:common` (IoDispatcher expect/actual + AppDispatchers + Logger)
- **INVERT**: replace all 3 remaining expect/actual seams with interfaces in `:core:common` + impl classes in `:composeApp/<plat>Main`, wired through Koin. Audit gains A26 + A27.
- **OOS**: move `AppException` + `BaseUseCase` to `:core:common`; rename package; add `build-logic/` convention plugin.
- **UI-OOS**: move `:core:ui`'s platform code to `:core:common/common/print/`; rename packages.
- **DATA-OOS**: move `:core:data`'s `PdfDownloader` expect/actual to `:core:common/common/platform/`.
- **FEAT-DECOUPLE**: `:features` drops direct deps on `:core:common` + `:core:data`. Per-feature DI modules bind only VMs.
- **A28 + typed errors (Phase W)**: every production error uses a typed `AppException` subclass.
- **Cart atomic refactor (`bedd1ad`, `27a4d01`)**: `CartRepository.state: StateFlow<CartState>` collapses two-flow combine into one atomic emission per operation.
- **Per-feature split arc (`5b4d0ed` → `9a76123`)** — the big one:
  - `5b4d0ed`: extract `:features:shared` (nav hub + 20 Route data objects). Foundation for everything that follows.
  - `26d9589`: pilot `:features:help` — proves the 6-step recipe
  - `5768ea8`: `:features:profile`
  - `049c7d1`: `:features:planning` (2 screens bundled)
  - `24e22be`: `:features:labels`
  - `d74a5e2`: `:features:offlinesync`
  - `65ce268`: `:features:saleshistory` + `:features:expiry` + `:features:auth` (3 in one commit; first batch with full test co-location)
  - `57e12d2`: `:features:movements` + `:features:bulkimport` + `:features:stockcount`
  - `f788f27`: `:features:customers` + `:features:suppliers` + `:features:imports`
  - `30f9c4f`: `:features:users` + `:features:reports` + `:features:ky`
  - `9c34761`: `:features:stock` (16 prod + 30 tests co-located)
  - `b643954`: `:features:sell` (39 prod, the heaviest)
  - `2a9f822`: `:features:settings` (the planned finale)
  - `9a76123`: extract `:features:test-fixtures` for 14 shared fakes, co-locate 18 cross-cutting tests, **delete `:features` module entirely**. Audit rule grows the `/features/test-fixtures/` A28 exclusion.

## Out of scope (deferred)

- AGP 9 migration (currently AGP 8.13 — KMP plugin compatibility warnings shown in `:composeApp:check` are expected)
- iOS Framework split per-feature (single `ComposeApp.framework` at `:composeApp`)
- Per-feature CI matrix wiring (`.github/workflows/check.yml` runs everything as a single job — could fan out by changed module path)
- iPad popover anchor for `UIActivityViewController` share sheet (KVC fallback path documented in `iosMain/.../FileDownloaderImpl.kt` commit message `96f9407`)
- `PharmMiniBarChart` tap-for-tooltip
- Bulk-import drag-drop on JVM/Web platforms
