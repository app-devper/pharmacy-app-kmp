# Module graph — pharmacy-app KMP companion

6-module Gradle layout. Dependency direction flows **inward only**: from
entry point `:composeApp` down through `:features` → `:core:{ui,data}` →
`:core:domain` → `:core:common`, with `:core:common` at the bottom depending
on nothing else in the project.

## Graph

```
                        ┌────────────────┐
                        │  :composeApp   │   entry point
                        │  (app shell)   │   App / AppViewModel / AppNavHost
                        └────────┬───────┘   AppModule + CoreModule
                                 │
              ┌──────────────────┼──────────────────┐
              ▼                  ▼                  ▼
       ┌────────────┐     ┌────────────┐     ┌────────────┐
       │ :features  │     │ :core:ui   │     │ :core:data │
       │ 17 feats   │     │ theme +    │     │ repo impls │
       │ + DI + VMs │     │ DS + base  │     │ + transport│
       └─────┬──────┘     └─────┬──────┘     └─────┬──────┘
             │                  │                  │
             └──────────────────┼──────────────────┘
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
                        │   deps;        │  PdfDownloader interface
                        │   commonMain   │  ReceiptPrinter interface
                        │   only)        │  BaseUseCase / BaseSyncUseCase
                        └────────────────┘
   ↑ impls of PdfDownloader / ReceiptPrinter live in :composeApp/<plat>Main
```

## Dependency matrix

| Module          | depends on                                                       |
|-----------------|------------------------------------------------------------------|
| `:composeApp`   | `:features`, `:core:ui`, `:core:data`, `:core:domain`, `:core:common` |
| `:features`     | `:core:ui`, `:core:domain` (only — does NOT directly depend on `:core:common` or `:core:data`) |
| `:core:ui`      | `:core:domain`, `:core:common` (+ compose)                       |
| `:core:data`    | `:core:domain`, `:core:common` (+ ktor + multiplatform-settings + koin-core) |
| `:core:domain`  | `api(:core:common)` (re-exported so consumers reach `AppDispatchers`/`AppException`/`BaseUseCase` etc. transitively) (+ kotlinx) |
| `:core:common`  | _none_ (kotlinx-only — host of every expect/actual in the project) |

**Note on `:features` decoupling**: `:features` cannot reach `:core:data` symbols
at all (no direct dep, no transitive path — `:core:domain` does not re-export
`:core:data`). Repository bindings (`<X>RepositoryImpl bind <X>Repository::class`
+ `<X>Api`) live in `:core:data`'s own Koin module (`dataModule` at
`core/data/.../data/di/DataModule.kt`), which `:composeApp` includes via
`appModule.includes(dataModule, …)`. Features inject `<X>Repository` interfaces
from `:core:domain` — they never name a concrete `RepositoryImpl`.

`:features` CAN still reach `:core:common` symbols (e.g. `AppDispatchers` in VM
tests, `printReceipt` in `CheckoutViewModel`) via `:core:domain`'s `api()`
re-export. This is intentional — the dep direction is correct (features → domain → common),
just declared once at the layer that needs to re-export.

**Note on DI module layout**: Each "owning" layer maintains its own Koin module:
- `:core:domain` → `domainModule` (76 bindings: 5 providers + 1 parser + 68 use cases + 2 settings use cases). Mirrors the rule that all `<X>UseCase` and `<X>Provider` classes live in `:core:domain`.
- `:core:data` → `dataModule` (33 bindings: 15 `<X>Api` + 18 `<X>RepositoryImpl bind <X>Repository::class`).
- `:features/di/<Feature>Module.kt` → ONLY VM `factoryOf` bindings now. The 10 per-feature DI files total ~140 lines.
- `:composeApp/di/AppModule.kt` → composition root: `includes(coreModule, domainModule, dataModule, authModule, customersModule, …)`. Order matters in principle (providers must be in scope before use cases that consume them), but Koin resolves lazily so include order doesn't bite in practice.

## What lives where

### `:composeApp` — app shell only (only module with platform source folders)

After the INVERT refactor, `:composeApp` is **the** only module in the project that has
`androidMain` / `iosMain` / `jvmMain` / `wasmJsMain` source sets. It owns:
- The Compose Application entry points (Main*.kt)
- Every platform impl of cross-cutting interfaces (PdfDownloaderImpl, ReceiptPrinterImpl)
- Per-platform Koin module bindings (Settings, HttpClient engine, AppDispatchers,
  PdfDownloader, ReceiptPrinter)

| Path | Contents |
|---|---|
| `commonMain/.../App.kt` | Root composable |
| `commonMain/.../presentation/AppViewModel.kt`, `AppUiState.kt` | Global VM (login state + offline queue badge) |
| `commonMain/.../presentation/navigation/AppNavHost.kt` | Wires all 17 feature `*Graph` extensions |
| `commonMain/.../di/AppModule.kt` | `includes(commonModule, domainModule, dataModule, …)` + binds `AppViewModel` |
| `commonMain/.../di/CoreModule.kt` | **(removed in REC-A)** — every infra binding now lives in its layer's own Koin module + per-platform module in Main*.kt |
| `androidMain/.../MainActivity.kt`, `PharmacyApplication.kt` | Android entry; `PharmacyApplication.androidPlatformModule` binds `Settings` + `HttpClient(OkHttp)` + `AppDispatchers(IO)` + `PdfDownloaderImpl(context)` + `ReceiptPrinterImpl()` |
| `androidMain/.../platform/{PdfDownloaderImpl,ReceiptPrinterImpl}.kt` | Android impls of the `:core:common` interfaces (currently both no-op + Log warning — wired so a future Android print/save story doesn't require any interface change) |
| `iosMain/.../MainViewController.kt` | iOS entry; `iosPlatformModule` mirrors Android with Darwin engine + `Dispatchers.Default` for IO slot |
| `iosMain/.../platform/{PdfDownloaderImpl,ReceiptPrinterImpl}.kt` | iOS impls (no-op + NSLog today) |
| `jvmMain/.../Main.kt` | Desktop entry; `jvmPlatformModule` with Java engine + real `Dispatchers.IO` + Desktop printer + ~/Downloads PDF saver |
| `jvmMain/.../platform/{PdfDownloaderImpl,ReceiptPrinterImpl}.kt` | JVM impls — full `java.awt.print.PrinterJob` receipt + writes PDF to Downloads & opens via `Desktop` |
| `wasmJsMain/.../Main.kt` | Web entry; `webPlatformModule` with JS engine + `Dispatchers.Default` for IO slot |
| `wasmJsMain/.../platform/{PdfDownloaderImpl,ReceiptPrinterImpl}.kt` | wasmJs impls — base64 anchor download + iframe `window.print()` for receipts |

All platform impls live under one package per platform: `app.devper.pharm.platform.{PdfDownloaderImpl,ReceiptPrinterImpl}` — single per-platform "platform-impl" folder.

### `:core:common` — pure cross-cutting infra (commonMain + commonTest only, zero project deps)

After the INVERT refactor, `:core:common` has **no platform source folders** at all. Every
platform-bound concern is abstracted as an `interface` here; impls live in
`:composeApp/{platform}Main/` and are bound via Koin in each Main*.kt's platform module.
`:core:common` now joins `:core:domain`/`:core:ui`/`:core:data` as commonMain+commonTest only.

| Path | Package | Contents |
|---|---|---|
| `common/AppDispatchers.kt` | `app.devper.pharm.common` | `data class AppDispatchers(main, io, default)` — no `real()` factory; constructed in per-platform module with platform-appropriate dispatchers |
| `common/Logger.kt` | `app.devper.pharm.common` | `Logger` interface + `PrintlnLogger` impl (no platform binding) |
| `common/AppException.kt` | `app.devper.pharm.common` | Sealed `AppException` + 7 typed subclasses (`AuthException`, `ForbiddenException`, `NotFoundException`, `ConflictException`, `NetworkException`, `ServerException`, `ValidationException`) — domain error language; enforced by A28 (no generic exceptions in production code) |
| `domain/usecase/BaseUseCase.kt` | `app.devper.pharm.domain.usecase` | `BaseUseCase<P,R>` + `BaseSyncUseCase<P,R>` framework (split package with `:core:domain`; intentional — minimizes per-use-case imports) |
| `common/print/ReceiptTemplate.kt` | `app.devper.pharm.common.print` | `data class ReceiptTemplate` + `ReceiptLine` only (no `expect fun`) |
| `common/print/ReceiptPrinter.kt` | `app.devper.pharm.common.print` | **interface** `ReceiptPrinter { fun print(template): Boolean }` — impl per platform in `:composeApp` |
| `common/platform/PdfDownloader.kt` | `app.devper.pharm.common.platform` | **interface** `PdfDownloader { suspend fun save(filename, bytes): Result<String> }` — impl per platform in `:composeApp` |
| `common/di/CommonModule.kt` | `app.devper.pharm.common.di` | Koin module — binds `Logger` only (1 binding). `AppDispatchers` / `PdfDownloader` / `ReceiptPrinter` now bound per-platform in `:composeApp`. |

The only KMP module with platform source folders for actual implementations.
Any future expect/actual that isn't UI- or data-specific lives here.

### `:core:domain` — pure domain (commonMain + commonTest only)

`:core:domain` has **no** `androidMain` / `iosMain` / `jvmMain` / `wasmJsMain`
folders. Every actual lives in `:core:common`. Verify with:
`find core/domain/src -name "*.kt" | grep -vE "/commonMain/|/commonTest/"`
→ must be empty.

| Path | Contents |
|---|---|
| `model/` | Drug, Customer, Supplier, Sale, Cart*, Lot, Adjustment, StockCount, PurchaseOrder, KyEntry*, KyForms, Settings, User, Role, … (26 files, flat — many are cross-cutting) |
| `param/<feature>/` | All `*Param` inputs grouped per feature (21 files across 10 folders) |
| `repository/<feature>/` | Repo interfaces grouped per feature (18 files across 10 folders; impls live in `:core:data`) |
| `usecase/<feature>/` | 68 use cases grouped per feature: `auth/` (2), `customers/` (4), `suppliers/` (4), `inventory/` (14), `ky/` (7), `offlinesync/` (3), `purchasing/` (7), `reports/` (6), `sales/` (19), `settings/` (2). Each class extends `BaseUseCase` / `BaseSyncUseCase` from `:core:common`. Package stays `app.devper.pharm.domain.usecase` (path-to-package mismatch by convention — same as `:core:common`'s `BaseUseCase` and the MN/UI-OOS pattern). |
| `parser/` | 4 builders |
| `util/` | `BarcodeMatcher`, `DrugSearch`, `SaleReturnQty`, … |
| `pricing/` | `resolvePrice` + Tier |
| `event/` | `StockChangeBus` |
| `observer/` | 5 `*Provider` classes (Phase BB) |
| `di/<Feature>DomainModule.kt` × 10 | Per-feature Koin modules: `authDomainModule`, `customersDomainModule`, `suppliersDomainModule`, `inventoryDomainModule`, `kyDomainModule`, `offlineSyncDomainModule`, `purchasingDomainModule`, `reportsDomainModule`, `salesDomainModule`, `settingsDomainModule`. Each binds its providers + use cases. |
| `di/DomainModule.kt` | Composer (18 lines): `domainModule = module { includes(authDomainModule, …) }`. `:composeApp` includes this from `appModule`. Total bindings across the 10 sub-modules: 77 (5 providers + `StockChangeBus` + `BulkImportJsonParser` + 70 use case factories). |

### `:core:ui` — shared compose infra (commonMain + commonTest only)

`:core:ui` has **no** `androidMain` / `iosMain` / `jvmMain` / `wasmJsMain` folders —
every platform-bound piece (the print expect/actual quad + ReceiptTemplate data)
moved to `:core:common`. The only thing still in `:core:ui/print/` is the pure
Compose-free `ReceiptBuilder` that turns a `Sale` into a `ReceiptTemplate`.

Package root is `app.devper.pharm.ui.*` (not `presentation.*` — that namespace
is owned by `:features` now). Resource accessor: `app.devper.pharm.ui.resources`.

| Path | Contents |
|---|---|
| `ui/theme/` | Color, Theme, Typography, DesignTokens (PharmTokens) |
| `ui/designsystem/` | `PharmButton`, `PharmBadge`, `PharmTextField`, `PharmModal`, `PharmTopbar`, `PharmSidebar`, `MetricCard`, `DrugCard`, `FormField`, `KyBadge` |
| `ui/common/` | `BaseUiState`, `BaseViewModel`, `BaseFormViewModel`, `BaseFormUiState`, `RunVmTest` |
| `ui/components/` | `AppShell`, `ErrorBottomSheet`, `WindowSize` |
| `ui/format/` | `Money.kt` (formatBaht / formatBahtCurrency / fmtBaht) |
| `ui/scanner/` | `BarcodeScannerModifier` (HID listener) |
| `ui/print/` | `ReceiptBuilder` (pure — `ReceiptTemplate` lives in `:core:common`) |
| `ui/help/MarkdownText.kt` | Markdown renderer |
| `composeResources/font/sarabun_*.ttf` | 5 weights (Phase D) |

`compose.resources { packageOfResClass = "app.devper.pharm.ui.resources" }`

### `:core:data` — repository impls + transport (commonMain + commonTest only)

`:core:data` has **no** `androidMain` / `iosMain` / `jvmMain` / `wasmJsMain`
folders — every platform-bound piece (PdfDownloader expect/actuals) moved to
`:core:common`. Mirrors `:core:domain` and `:core:ui`. The ktor engines
(`ktor-client-okhttp`/`darwin`/`java`/`js`) live in `:composeApp`'s per-platform
source sets, not here.

| Path | Contents |
|---|---|
| `data/network/` | `HttpClient` builder, `AppJson`, `ApiConfig`, `AppExceptions`, `HttpResponseValidator` |
| `data/storage/` | `TokenStorage`, `ParkedCartStorage`, `OfflineSaleQueueImpl` (multiplatform-settings adapters) |
| `data/repository/` | All `*RepositoryImpl` (Phase Q — no `runCatching` here) |
| `data/remote/api/` | All `*Api` interfaces + endpoint paths |
| `data/remote/dto/` | DTOs + Request / Response types. Two rules: (1) Kotlin field names are camelCase (`val sellPrice` not `val sell_price`); (2) every field carries `@SerialName("wire_name")` — even when wire matches Kotlin name. Single-line `@SerialName("sell_price") val sellPrice: …` convention. Enforced by A24 + A25 in review skill + `auditArchitecture` Gradle task. |
| `data/di/DataModule.kt` | Koin `dataModule` with 33 bindings: every `<X>Api` (15) + every `<X>RepositoryImpl bind <X>Repository::class` (18). `:composeApp` includes this from `appModule`. |

### `:features` — all 19 features (Profile + Users added post-architecture)

| Folder | Owns |
|---|---|
| `auth/` | `LoginScreen`, `LoginViewModel`, `AuthNavGraph`, `AuthRoutes` |
| `sell/` | `SellScreen`, `CartScreen`, 5 sibling VMs, components |
| `saleshistory/` | history list + detail |
| `customers/` | list + form + detail + picker |
| `suppliers/` | list + form |
| `stock/` | stock list + `DrugForm` + `DrugLots` + `StockAdjustments` |
| `stockcount/` | physical-count form + list |
| `imports/` | purchase orders (PO list + form) |
| `bulkimport/` | bulk import wizard |
| `movements/` | stock movements log |
| `expiry/` | expiry tracker |
| `planning/` | `LowStock` + `ReorderSuggestions` |
| `reports/` | Reports + Profit + Eod |
| `ky/` | KHY9 + KyList (for 10/11/12/13) |
| `settings/` | settings screen |
| `offlinesync/` | offline queue monitor |
| `help/` | user guide markdown viewer |

Co-located per feature:
- `presentation/<feature>/*Screen.kt + *ViewModel.kt + *UiState.kt + *NavGraph.kt + *Routes.kt`
- `di/<Feature>Module.kt` (11 files total — some features share a module)
- `commonTest/.../<feature>/*ViewModelTest.kt` (21 test files, 205 tests today)
- `commonTest/.../fakes/Fake*Repository.kt` (test doubles)

Common helpers in `:features`:
- `presentation/navigation/ShelledScreen.kt` (top-level chrome — drawer + topbar)

`compose.resources { packageOfResClass = "app.devper.pharm.features.resources" }`
- `composeResources/files/user_guide.md` (loaded by `HelpViewModel` via `Res.readBytes`)

## Forbidden imports (P0)

Audited by `pharmacy-kmp-review` skill and **enforced at build time** by the
`auditArchitecture` Gradle task (in `build-logic/.../pharmacy.architecture.audit.gradle.kts`).
The task runs as part of `:composeApp:check` and on every PR via
[`.github/workflows/check.yml`](.github/workflows/check.yml) on `macos-latest`, so any stale
import is rejected before merge:

| From            | To                | Status |
|-----------------|-------------------|--------|
| `:core:common`  | any project module| ❌ P0  |
| `:core:domain`  | `:core:ui` / `:core:data` / `:features` / `:composeApp` | ❌ P0 |
| `:core:ui` / `:core:data` | `:features`  | ❌ P0  |
| `:core:*`       | `:composeApp`     | ❌ P0  |
| `:features`     | `:core:data` (any class — even via DI)  | ❌ P0 (use `:core:domain` repo interfaces; bindings live in `:core:data/di/DataModule.kt`) |
| `:features`     | `:composeApp`     | ❌ P0  |
| `:features.<X>` | `:features.<Y>`   | ⚠️ P1 (same module today; would become P0 if split per-feature later) |
| Any module other than `:composeApp` | `androidMain` / `iosMain` / `jvmMain` / `wasmJsMain` source folder | ❌ A26 (only `:composeApp` may have platform source sets; everything else is commonMain + commonTest) |
| Anywhere in the repo | `expect class` / `expect fun` / `expect val` declarations | ❌ A27 (the project uses interface + impl pattern instead — interface in `:core:common`, impls in `:composeApp/<plat>Main` bound via Koin) |

## Test layout

| Module          | Test source set       | Test count today (jvmTest) |
|-----------------|-----------------------|----------------------------|
| `:core:common`  | `commonTest`          | 16 (`AppException` / `Logger` / `BaseUseCase` / `BaseSyncUseCase`) |
| `:core:domain`  | `commonTest`          | 71 (model / parser / util / pricing + `UmRoleValidator` 4×4 actor×target) |
| `:core:ui`      | `commonTest`          | 22 (`Money` / `fmtBaht` / `BaseViewModel` / `BaseFormViewModel`) |
| `:core:data`    | `commonTest`          | 20 (`AppJson` / `ApiConfig` / `HttpResponseValidator` / `OfflineSaleQueueImpl`) |
| `:features`     | `commonTest`          | 229 (VM unit tests across 38 files — incl. Profile + Users) |
| `:composeApp`   | `commonTest`          | 1 (`AppModuleWiringTest` — resolves every VM via Koin) |

**Total**: **359** unique tests on JVM (the Android `testDebugUnitTest` target runs the same `commonTest` sources separately, so the doubled count is ~520 on a full `:composeApp:check`).

Run everything:
```bash
./gradlew :features:jvmTest :core:domain:jvmTest :core:common:jvmTest \
          :composeApp:testDebugUnitTest \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs
```

## Adding new code — quick lookup

| You want to add… | Goes in… |
|---|---|
| A new expect/actual (dispatcher / logger / platform plumbing) | `:core:common` |
| A new domain model / use case | `:core:domain` |
| A new repository interface | `:core:domain/repository/` |
| The impl for that interface | `:core:data/repository/` |
| A new DTO / API endpoint | `:core:data/remote/{dto,api}/` |
| A new design primitive | `:core:ui/presentation/designsystem/` |
| Anything theme / color / token | `:core:ui/presentation/theme/` |
| A new feature screen + VM | `:features/presentation/<feature>/` |
| Its DI bindings | `:features/di/<Feature>Module.kt` |
| Its NavGraph + Routes | `:features/presentation/<feature>/` |
| A new VM test | `:features/commonTest/.../<feature>/` |
| A test double | `:features/commonTest/.../fakes/` |
| Wiring a feature into nav | `composeApp/.../presentation/navigation/AppNavHost.kt` |
| Wiring a feature into DI | `composeApp/.../di/AppModule.kt` (`includes(…)`) |

## Why this split

- **`:core:common` separate**: gives expect/actual a dedicated home so
  `:core:domain` can stay 100% commonMain (no per-target folders at all).
  Any new platform plumbing (dispatcher, logger, time, random, etc.) drops
  here without re-introducing platform folders into the domain layer.
- **`:core:domain` separate**: enforce the Phase S inward-only rule via the
  build, not just code review. Kotlinx-only means it compiles in ~2 seconds.
- **`:core:ui` separate**: theme + design system + VM base are stable and
  shared by every feature — keeping them in their own module gives Gradle a
  cache boundary so feature edits don't recompile the design system.
- **`:core:data` separate**: ktor / multiplatform-settings / repository
  impls are infrastructure — features depend on the interfaces in
  `:core:domain`, not on the impls, so swapping transports doesn't touch
  presentation code.
- **`:features` as one module (not 17)**: every feature has the same
  dependency shape (`:core:*`), so splitting per-feature would create 17
  near-identical `build.gradle.kts` files for marginal compile-isolation
  benefit. The folder structure (`presentation/<feature>/`) is already
  per-feature, so future split → `:features:<X>` is mechanical.
- **`:composeApp` slim**: only 11 source files. The entire app is composed
  here — DI, nav, theme application — and nothing else lives here. This
  makes the entry trivially diff-able when modules below change.

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

**`pharmacy.kmp.library.gradle.kts`** (base) configures:
- `org.jetbrains.kotlin.multiplatform` + `com.android.library` plugins
- `jvmToolchain(17)`
- 5 targets: `androidTarget()`, `jvm()`, `iosX64()`, `iosArm64()`, `iosSimulatorArm64()`, `wasmJs { browser() }`
- `commonTest` gets `kotlin.test` automatically
- `android { compileSdk; minSdk; compileOptions JDK 17 }`

**`pharmacy.kmp.compose.library.gradle.kts`** (extends base) additionally adds:
- `id("pharmacy.kmp.library")` (inherits everything above)
- `org.jetbrains.compose` + `org.jetbrains.kotlin.plugin.compose` plugins
- Common compose deps to `commonMain`: `runtime`, `foundation`, `material3`, `materialIconsExtended`, `ui`, `components.resources`, `components.uiToolingPreview`
- `compose.resources { publicResClass = true; generateResClass = Always }` (each module just sets `packageOfResClass`)

**`pharmacy.architecture.audit.gradle.kts`** adds the `auditArchitecture` task and wires it into `:composeApp:check`.

Each library module's `build.gradle.kts` then collapses dramatically — `:core:ui` is 32 lines, `:features` is 43 lines, `:core:common` / `:core:domain` / `:core:data` are 18–31 lines. Apply via:

```kotlin
plugins {
    id("pharmacy.kmp.library")           // for :core:common, :core:domain, :core:data
    // or
    id("pharmacy.kmp.compose.library")   // for :core:ui, :features
    // + module-specific plugins (kotlin.serialization, …)
}
```

`:composeApp` is **not** on the convention plugins — it's an Android
Application (not Library) with its own structure (compose desktop block,
wasmJs executable, per-platform Main*.kt + per-platform Koin module). It stays
standalone. It does apply `pharmacy.architecture.audit` to host the audit task.

Wiring: `settings.gradle.kts` has `pluginManagement { includeBuild("build-logic") }`.

## Migration history

- **Phase S**: extract `:domain` module from monolithic `:composeApp`
- **MM-1**: rename `:domain` → `:core:domain`; create `:core:ui` + `:core:data`
- **MM-2**: create `:features` and move all 17 feature folders + 21 test
  files + 10 DI modules
- **MM-3**: slim `:composeApp` to 11 files (entry only)
- **MM-4**: docs + skills
- **MN**: extract `:core:common` (IoDispatcher expect/actual + AppDispatchers
  + Logger); `:core:domain` becomes commonMain + commonTest only
- **INVERT**: replace all 3 remaining expect/actual seams (IoDispatcher, PdfDownloader,
  printReceipt) with **interfaces in `:core:common`** + **impl classes in
  `:composeApp/<plat>Main`**, wired through Koin in each Main*.kt's platform module.
  Result: `:core:common` becomes commonMain + commonTest only — joining
  `:core:domain` / `:core:ui` / `:core:data`. **`:composeApp` is now the only
  module with platform source folders.** Audit gains A26 (platform-folder
  ownership) + A27 (no `expect` declarations anywhere).
- **OOS**: move `AppException` + `BaseUseCase` / `BaseSyncUseCase` to
  `:core:common`; rename package `app.devper.pharm.domain.common` →
  `app.devper.pharm.common` (82 sites); add `build-logic/` convention plugin
  (each KMP library `build.gradle.kts` drops to 15–55 lines)
- **UI-OOS**: move `:core:ui`'s platform code (`printReceipt` expect + 4
  actuals + `ReceiptTemplate` data) to `:core:common/common/print/`;
  rename `:core:ui`'s packages `app.devper.pharm.presentation.*` →
  `app.devper.pharm.ui.*` for the 8 sub-packages (theme, designsystem,
  common, components, format, scanner, help, print) — ~70 file edits in
  `:core:ui` + ~150 importer updates across `:features` + `:composeApp`.
  `:core:ui` now has commonMain + commonTest only (mirrors `:core:domain`).
  Resource accessor renamed `app.devper.pharm.core.ui.resources` →
  `app.devper.pharm.ui.resources`. Split package between `:core:ui.help`
  (`MarkdownText`) and `:features.presentation.help` (Help feature) is
  resolved — no more shared namespace.
- **DATA-OOS**: move `:core:data`'s platform code (`PdfDownloader` expect +
  4 actuals) to `:core:common/common/platform/`; `:core:data` becomes
  commonMain + commonTest only (mirrors `:core:domain` + `:core:ui`). All
  ktor engine deps now live in `:composeApp`'s per-platform source sets
  only — `:core:data` only declares the generic `ktor-client-core` + bundle.
- **FEAT-DECOUPLE**: `:features` drops direct deps on `:core:common` +
  `:core:data`. `:core:domain` promotes `:core:common` from `implementation`
  to `api` so features (and their VM tests) transitively reach
  `AppDispatchers` / `AppException` / `BaseUseCase` / `printReceipt` /
  `ReceiptTemplate`. The 33 RepositoryImpl + Api Koin bindings move from
  the 11 per-feature DI modules into a single `dataModule` at
  `core/data/.../data/di/DataModule.kt`; `:composeApp`'s `AppModule`
  composes it via `includes(dataModule, …)`. Feature DI modules now bind
  only use cases, VMs, providers, and parsers.

## Out of scope (deferred)

- AGP 9 migration (currently AGP 8.13)
- iOS Framework split per-feature (single `ComposeApp.framework` at `:composeApp`)
- Future split `:features` → `:features:<X>` (structure inside is ready)
- Convention plugin in `build-logic/` (5 modules don't need it)
- Per-feature `composeResources/files/` (only `user_guide.md` exists today, in `:features`)
