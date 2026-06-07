# CLAUDE.md — pharmacy-app KMP companion

Project-scoped instructions for Claude Code when working in
`/Users/admin/ProjectPos/pharmacy-app/app-kmp/`. Applies to the KMP
companion app (Compose Multiplatform — **26-module Gradle layout**):
`:composeApp` + `:core:{common,domain,ui,data}` +
`:features:test-fixtures` + 20 `:features:<x>` modules. (`:features:shared` was
removed once the app shell moved to `:composeApp` — see Navigation below.)

## Module structure

```
:composeApp                              entry point + ONLY module with platform source folders
  ├─ App.kt + AppViewModel
  ├─ presentation/navigation/AppNavHost.kt   outer NavHost: authNav (Login) + composable<MainRoot>{ MainShell }
  ├─ presentation/navigation/MainNav.kt      MainRoot route + MAIN_NAV_TABLE + DEST_INFO (title+sectionKey
  │                                          per destination) + MainShell — renders AppShell ONCE around a
  │                                          nested NavHost that composes all 20 <x>Nav builders
  ├─ MainActivity / MainViewController / Main (jvm/wasm)
  ├─ di/AppModule.kt                     composition root — includes(commonModule, domainModule, dataModule,
  │                                       + 20 per-feature modules)
  ├─ <plat>Main/platform/FileDownloaderImpl.kt × 4   platform impls of FileDownloader interface
  ├─ <plat>Main/platform/ReceiptPrinterImpl.kt × 4   platform impls of ReceiptPrinter interface
  └─ <plat>Main bindings: each Main*.kt's platform Koin module binds Settings + HttpClient engine +
        AppDispatchers (platform-appropriate IO) + FileDownloaderImpl + ReceiptPrinterImpl
  deps: :core:{common,domain,ui,data} + all 20 :features:<x>

:core:common                             interfaces + pure infra (commonMain + commonTest only)
  ├─ common/ AppDispatchers (data class only, constructed per-platform)
  ├─ common/ Logger, PrintlnLogger
  ├─ common/ AppException + 9 typed subclasses (Auth, Forbidden, NotFound, Conflict,
  │                                              Network, Server, Validation, Storage,
  │                                              UnsupportedPlatform)
  ├─ common/print/ ReceiptTemplate (data) + ReceiptPrinter (interface)
  ├─ common/platform/ FileDownloader (interface) + MimeType constants
  ├─ common/di/CommonModule.kt           (Koin module — binds Logger only)
  ├─ domain/usecase/ BaseUseCase + BaseSyncUseCase
  package: app.devper.pharm.common (+ app.devper.pharm.domain.usecase for BaseUseCase)
  deps: kotlinx only (zero project deps) + koin-core
  rule: NO platform source folders. Every platform-bound concern is an interface here;
        impls live in :composeApp/<plat>Main and are Koin-bound per platform.

:core:domain                             pure domain — commonMain + commonTest only
  ├─ model/ param/ repository/ usecase/
  ├─ extension/ event/ observer/
  ├─ di/DomainModule.kt                  (Koin module — 76+ bindings via 10 sub-modules)
  deps: :core:common (+ kotlinx + koin-core)
  rule: ไม่มี androidMain / iosMain / jvmMain / wasmJsMain folder
  note: api(:core:common) — re-exports so :features:<x> เห็น :core:common ทาง transitively

:core:ui                                 shared compose infra (commonMain + commonTest only)
  ├─ ui/theme/ designsystem/ common/ components/
  ├─ ui/format/ scanner/ print/ help/
  ├─ composeResources/font/sarabun_*.ttf
  packages: app.devper.pharm.ui.*
  packageOfResClass = "app.devper.pharm.ui.resources"
  deps: compose + :core:common + :core:domain
  rule: ไม่มี platform source folders

:core:data                               repository impls + transport (commonMain + commonTest only)
  ├─ data/network/ storage/ repository/
  ├─ data/remote/api/ remote/dto/
  ├─ data/di/DataModule.kt               (Apis + RepositoryImpls — :composeApp includes it)
  deps: ktor + multiplatform-settings + koin-core + :core:common + :core:domain
  rule: ไม่มี platform source folders

:features:test-fixtures                  shared test doubles (commonMain only, test-only module)
  ├─ domain/repository/Fake{Cart,Customer,Drug,Ky,Label,OfflineSaleQueue,
  │                          Profile,PurchaseOrder,Reports,Sale,Settings,StockCounts,
  │                          Supplier,UiPreferences,Users}Repository.kt   (15 fakes)
  deps: :core:common + :core:domain + kotlinx-coroutines-core only
  rule: Fakes live in commonMain (not commonTest) so any feature's commonTest
        can `implementation(project(":features:test-fixtures"))` and import.
        A28 audit rule excludes /features/test-fixtures/ — fakes throw
        RuntimeException as a deliberate test signal.

:features:<x>                            20 per-feature modules
  ├─ build.gradle.kts                    pharmacy.kmp.compose.library
  ├─ src/commonMain/kotlin/app/devper/pharm/
  │   ├─ di/<Feature>Module.kt           ONLY VM `factoryOf` bindings
  │   ├─ presentation/<feature>/         Screen/Content/Callbacks/ViewModel/
  │   │                                  UiState + section/components/
  │   │                                  sibling subdirs as needed
  │   └─ presentation/<feature>/navigation/<Feature>Nav.kt   @Serializable route objects +
  │                                      fun NavGraphBuilder.<x>Nav(...) (NO ShelledScreen — pure
  │                                      route→screen; package app.devper.pharm.presentation.<feature>)
  ├─ src/commonTest/kotlin/...           (when tests exist)
  │   └─ presentation/<feature>/         VM tests + co-located fakes (when single-consumer)
  └─ composeResources/                   (only :features:help ships an asset today)
  deps: :core:domain + :core:ui (+ kotlinx-datetime when needed)
  test deps: :features:test-fixtures (when tests use shared fakes)

  The 20 modules: auth · bulkimport · customers · expiry · help · imports · ky ·
                  labels · movements · offlinesync · planning · profile · reports ·
                  saleshistory · sell · settings · stock · stockcount · suppliers · users
```

### Navigation (two-level NavHost + single shell)

`:composeApp` owns navigation. `AppNavHost` is the OUTER `NavHost(startDestination = Login)`
with exactly two destinations: **`authNav`** (the `Login` screen, no shell) and
**`composable<MainRoot> { MainShell(...) }`**. `LaunchedEffect(isLoggedIn)` swaps between
`Login` and `MainRoot` (clearing the back stack).

`MainShell` (in `composeApp/.../navigation/MainNav.kt`) renders the `AppShell`
(sidebar/topbar) **once** with its own nested `rememberNavController()`, and its content slot is
a nested `NavHost(startDestination = Sell)` that composes **every `<x>Nav` builder**. So each
feature's main page AND its sub-pages (forms/details) live under the one shell — the sidebar
persists across all of them. There is **no per-feature `ShelledScreen`** anymore.

- Active sidebar item + topbar title are derived from `nestedNav.currentBackStackEntryAsState()`
  via the `DEST_INFO` map in `MainNav.kt` (`route qualified name → (title, sectionKey)`). A
  sub-page maps to its parent section's key so the section stays highlighted; orphans/Profile
  map to `null` (no highlight).
- Each feature exposes `fun NavGraphBuilder.<x>Nav(...)` in `presentation/<x>/navigation/<X>Nav.kt`
  (route objects + builder, no shell). Leaf features take no params; features with sub-pages take
  `navController`; cross-feature jumps take a hoisted `() -> Unit` (only `stockNav`'s
  `onOpenReorderSuggestions` today — composeApp resolves it).

### Per-feature recipe (for adding a brand-new feature)

1. **Carve out the module** — `mkdir -p features/<feat>/src/commonMain/kotlin/app/devper/pharm/presentation/<feat>/`
2. **Add `features/<feat>/build.gradle.kts`** mirroring an existing leaf
   feature (`:features:help` is the smallest template: apply
   `pharmacy.kmp.compose.library`, depend on `:core:domain` + `:core:ui`).
   Set unique
   `compose.resources { packageOfResClass = "app.devper.pharm.features.<feat>.resources" }`
   only if the feature ships its own assets.
3. **Register in `settings.gradle.kts`** — append `:features:<feat>` to
   `include(...)`.
4. **Add `navigation/<Feat>Nav.kt`** inside the feature — the `@Serializable` route objects
   PLUS `fun NavGraphBuilder.<feat>Nav(navController: NavController)` declaring the
   `composable<Route>` destinations directly (NO `ShelledScreen`). Package stays
   `app.devper.pharm.presentation.<feat>`.
5. **Build the feature production code** in
   `features/<feat>/.../presentation/<feat>/` (Screen, Content, Callbacks,
   ViewModel, UiState) + `.../di/<Feat>Module.kt` with the VM `factoryOf` binding.
6. **Wire from `:composeApp`**:
   - `composeApp/build.gradle.kts`: `implementation(project(":features:<feat>"))`
   - `composeApp/.../navigation/MainNav.kt`: call `<feat>Nav(nestedNav)` inside `MainShell`'s
     nested `NavHost`; add a `DEST_INFO` entry (title + sectionKey) for each destination; add a
     `MainNavEntry(<Route>, …)` to `MAIN_NAV_TABLE` if it gets a sidebar item.
   - `composeApp/.../di/AppModule.kt`: append `<feat>Module` to `includes(...)`
   - Cross-feature jumps are **not** done by importing the other feature's route — hoist a
     `() -> Unit` callback into your `<feat>Nav(...)` and let `MainShell` supply
     `nestedNav.navigate(<OtherRoute>)`.

If the feature has tests using shared fakes, add to
`features/<feat>/build.gradle.kts`:

```kotlin
commonTest.dependencies {
    implementation(libs.kotlinx.coroutines.test)
    implementation(project(":features:test-fixtures"))
}
```

If a fake is single-consumer (only this feature's tests use it),
co-locate it under `features/<feat>/src/commonTest/.../domain/repository/`
instead of growing `:features:test-fixtures`.

**Forbidden (P0 — Kotlin compile errors + auditArchitecture task):**

The `auditArchitecture` Gradle task enforces 10 rules. Cross-feature
boundaries are also enforced implicitly by Kotlin's module system —
any import without a declared `project(...)` dep is a compile error.

Layering (forbidden imports):
- **A10**  `:core:*` importing from `:features:*`
- **A17**  stale `app.devper.pharm.domain.common` imports (post-split)
- **A19**  stale `:core:ui` package paths (pre-rename leftovers)
- **A20**  `:features:*` importing from `:core:data` (use `:core:domain` interface)
- **A23**  `:features:<x>/di/<X>Module.kt` importing non-VM types

Wire / source-set discipline:
- **A24**  DTO property missing explicit `@SerialName`
- **A25**  DTO property using snake_case Kotlin name
- **A26**  platform source folders outside `:composeApp`
- **A27**  `expect`/`actual` declarations anywhere in the project
- **A28**  generic `Exception` / `RuntimeException` / `IllegalStateException` in production

- `:core:common` ห้ามรู้จัก project module อื่นเลย (zero project deps)
- `:core:domain` ห้ามรู้จัก `:core:ui` / `:core:data` / `:features:*` / `:composeApp`
- `:core:*` ห้ามรู้จัก `:features:*` / `:composeApp`
- `:features:test-fixtures` ห้ามรู้จัก `:features:<x>` / `:composeApp`
  (same reason — and it should stay minimal, only depending on
  `:core:common` + `:core:domain` + kotlinx)
- `:features:<x>` ห้ามรู้จัก `:core:data` (\*RepositoryImpl, \*Api, DTO —
  features ใช้ Repository interface จาก :core:domain เท่านั้น; data
  bindings อยู่ใน :core:data/.../di/DataModule.kt ซึ่ง :composeApp include เอง)
- `:features:<x>` ห้ามรู้จัก `:composeApp`
- `:features:<x>` ห้ามรู้จัก `:features:<y>` ที่เป็น production code (รวมถึง Route
  object ของ feature อื่น — แต่ละ feature เป็นเจ้าของ route ตัวเองใน
  `presentation/<x>/navigation/`). ถ้าต้อง navigate ข้าม feature ให้ hoist callback
  `() -> Unit` ขึ้นไปที่ `<x>Graph(...)` แล้วให้ `:composeApp` AppNavHost เป็นผู้
  `navController.navigate(<OtherRoute>)` (composeApp เห็นทุก route). cross-feature
  ที่มีอยู่: `auth→Sell` (post-login) และ `stock→ReorderSuggestions` ทำผ่าน callback ทั้งคู่
- **A26**: เฉพาะ `:composeApp` เท่านั้นที่มี platform source folders
  (`androidMain` / `iosMain` / `jvmMain` / `wasmJsMain`); module อื่นเป็น
  `commonMain` + `commonTest` ล้วน ๆ
- **A27**: ห้ามมี `expect class` / `expect fun` / `expect val` ที่ไหนในโปรเจกต์;
  ใช้ interface ใน `:core:common` + impl ใน `:composeApp/<plat>Main` แทน
- **A28**: ห้าม throw generic `IllegalStateException` / `RuntimeException` /
  `Exception` ใน production code — ใช้ typed `AppException` subclass
  (`:features:test-fixtures` ได้รับ exemption — fakes throw RuntimeException
  เป็น test signal เจตนา)

## Code style — NO COMMENTS

**ห้ามใส่ comment ใด ๆ ลงในไฟล์ `.kt` ทุกชนิด** — รวมถึง:

- `//` line comments
- `/* ... */` block comments
- `/** ... */` KDoc on classes, functions, properties, parameters
- TODO / FIXME / NOTE / HACK markers
- File-level header banners

Code ต้อง **self-documenting** ผ่านชื่อที่ดี + type signatures + small focused
functions. ถ้ารู้สึกว่าต้องอธิบายอะไร → rename + refactor แทนการเขียน comment.

This rule applies to:
- Production code in `composeApp/src/*`, `core/{domain,ui,data}/src/*`, `features/*/src/*` (all 21 feature/test-fixtures modules)
- Test code in `*/src/commonTest`, `*/src/jvmTest`, `*/src/androidUnitTest`
- Fakes / fixtures / helpers in any test source set (including `:features:test-fixtures` despite its commonMain location)
- Build scripts (`build.gradle.kts`, `settings.gradle.kts`) — ถ้าต้อง explain
  ทำผ่าน clean structure แทน

**Exceptions** (case-by-case, ขอ confirm ก่อน):
- `@Suppress("...")` annotations — เป็น annotation ไม่ใช่ comment
- License header — ถ้า upstream library require (ไม่มีตอนนี้)
- Generated files under `build/` — ไม่ต้องแตะอยู่แล้ว
- Markdown / config / `.gradle.kts` plugin metadata — ไม่ใช่ Kotlin comment

When restyling / refactoring existing code: **strip comments as part of
the edit**. Don't preserve KDoc just because it was there. If a method's
purpose isn't clear from its name, rename the method.

### Why

- Phase-letter notes (M-CC) + split-arc commit history are living history
  in commit messages / PR descriptions, not buried in source
- Skill files (`pharmacy-kmp-*` SKILL.md) document patterns at a higher
  level; in-code comments duplicate that
- KMP cross-compiles 5 targets — every byte of source matters for context

## Project reminders (KMP-specific)

- **Stack**: Kotlin Multiplatform 2.3.21 / Compose Multiplatform 1.11.0 / AGP
  8.13.2 / Gradle 8.14.3
- **Targets**: `jvm`, `android`, `iosArm64`, `iosSimulatorArm64`, `wasmJs`
- **Modules (26)**: `:composeApp` (entry; owns the app shell + navigation), `:core:{common,domain,ui,data}` (4 core),
  `:features:test-fixtures` (test doubles),
  20 `:features:<x>` (auth, bulkimport, customers, expiry, help, imports, ky,
  labels, movements, offlinesync, planning, profile, reports, saleshistory,
  sell, settings, stock, stockcount, suppliers, users). See `MODULE_GRAPH.md`
  for the full dep matrix.
- **Convention plugins** (`build-logic/`): each KMP library applies one of
  `id("pharmacy.kmp.library")` (pure data/domain — `:core:common`, `:core:domain`,
  `:core:data`, `:features:test-fixtures`) or `id("pharmacy.kmp.compose.library")`
  (compose-aware — `:core:ui`, all 20 `:features:<x>`).
  The compose flavor inherits the base and additionally applies compose plugins +
  common compose deps + `compose.resources` defaults. `:composeApp` applies
  `pharmacy.architecture.audit` for the `auditArchitecture` task.
- **Test verify** (full sweep — used in this commit's verify):
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
  Quick smoke (runs the full dependent tree): `./gradlew :composeApp:check`.
  Project test count today: 759 `@Test` functions across 111 commonTest files
  (most concentrated in the 20 per-feature modules — `:features:sell` alone
  ships 83). Re-measure with
  `grep -rn '@Test' core features composeApp --include='*.kt' | wc -l`.
- **Design system**: tokens in `:core:ui` →
  `ui/theme/DesignTokens.kt`; primitives in
  `ui/designsystem/Pharm*.kt`
- **No M3 widgets** in net-new files: use `PharmButton` / `PharmBadge` /
  `PharmTextField` / `FormField` / `PharmModal` / `MetricCard` / `DrugCard` /
  `PharmTable` / `PharmStaticTable` / `PharmFilterChips` / `PharmIcons` (SVG vectors)
- **Page scaffold primitives** (use these so every screen looks the same):
  - `PharmListToolbar(title, subtitle, onBack, searchValue, filters, actions)` — the single
    page-header primitive for BOTH list pages and sub-pages. Used by every list screen
    (stock/expiry/movements/imports/customers/suppliers/users/saleshistory/stockcount/ky/
    planning + offlinesync/profit/settings). Pass `onBack` to render a back button before the
    title (sub-page mode — title/subtitle then always show, even below the 600dp width where
    list-page titles are otherwise hidden in favour of the shell topbar).
  - `PharmListResultLine(total, noun, visible, searching, trailing)` — "ทั้งหมด N <noun>"
    band; pass a `trailing` slot for per-page totals (e.g. saleshistory ยอดรวม, expiry stat).
  - **Sub-pages** (detail/form pages with a back button) use the SAME structure as main pages,
    NOT a separate scaffold (the old `PharmSubPage` was removed): wrap in
    `Column(Modifier.fillMaxSize().background(bgPage)) { PharmListToolbar(title, subtitle,
    onBack, actions); <content> }`. The content area is a `Column(Modifier.weight(1f).fillMaxWidth()
    [.verticalScroll(rememberScrollState())].padding(…))` — full-width like main pages (no
    centered max-width). Every detail/form page follows this (customers detail, imports detail,
    drug form, customer/supplier/user form, imports form, stock-count form, profile, drug
    lots/adjust/history, ky add pages, planning reorder, eod). `onBack` on `PharmListToolbar`
    renders a back button and forces the title to show at all widths.
  - `PharmFormCard(title, subtitle)` — form section card (rounded surface + 1dp border + h2
    title + optional subtitle + 16dp inner spacing). Wrap each form section in one;
    `UserForm/CustomerForm/SupplierForm/DrugForm/ImportForm/SettingsTab/Ky9-12Add` all use it.
  - `PharmSaveAction(saving, canSubmit, onSubmit, label)` — the save control that goes in the
    `actions` slot of a sub-page's `PharmListToolbar`. Forms no longer ship a bottom save bar.
  - `ReloadOnResume(onResume)` (in `ui/common/`) — wraps a `LifecycleEventObserver` to call
    `viewModel::reload` on `ON_RESUME`. Every list/dashboard screen uses it so a record
    added on a detail page shows up when you navigate back.
- **DTO field convention (camelCase Kotlin + explicit `@SerialName`)**: in
  `:core:data/.../data/remote/dto/` and `:core:data/.../data/storage/*Dto.kt`,
  every `@Serializable data class` property must:
  1. Use **camelCase** Kotlin field names — `val sellPrice: Double`, never `val sell_price: Double` (A25)
  2. Carry an explicit `@SerialName("wire_name")` — even when wire name matches Kotlin name (A24)
  Single-line format: `@SerialName("sell_price") val sellPrice: Double = 0.0,`. Both
  rules enforced by `auditArchitecture` Gradle task (in `build-logic/`).
- **Typed errors (A28)**: production code throws typed `AppException`
  subclasses, never raw `IllegalStateException` / `RuntimeException` /
  `Exception`. `BaseUseCase.invoke()` wraps once via `runCatching` and
  converts to `Result<R>` at the use case layer. Repositories return bare
  `T` and throw typed exceptions; the `:features:test-fixtures` module is
  exempted (fakes throw `RuntimeException("...")` as a deliberate test
  signal). Use Storage / UnsupportedPlatform subclasses for IO/platform
  failures in `:composeApp/<plat>Main/platform/`.

- **Money / Quantity value classes** (`:core:common/value/Money.kt` +
  `Quantity.kt`): every monetary field on a domain model / param is
  `Money` (wraps `Double`), every counted-stock field is `Quantity`
  (wraps `Int`). The compiler enforces that you can't add a price to a
  stock count, divide a price by another price by mistake, or pass a
  `Quantity` where a `Money` was expected. End-to-end through the
  domain layer: `Drug` / `AltUnit` prices + stock, `Sale` /
  `SaleSummary` / `SaleItemSnapshot` prices, `CartLine` math chain
  (`basePrice` / `unitPrice` / `effectiveUnitPrice` / `lineTotal`),
  `CartDiscount.Flat.amount` + `CartDiscount.apply()`, `CartSnapshot`
  / `ParkedCart` / `SellUiState` totals, `CheckoutParam` +
  `CheckoutLineParam` + `RunCheckoutParam`, `DrugLot` / `AddLotParam`,
  `PurchaseOrder` + `PurchaseOrderSummary` + `PurchaseOrderItem` +
  `PurchaseOrderItemInput`, `ReorderSuggestion`.
  - **DTO boundary stays Double/Int** (`@Serializable data class`
    properties on wire). Mappers wrap inbound (`Money(dto.sellPrice)`,
    `Quantity(dto.stock)`), unwrap outbound (`sellPrice.amount`,
    `stock.value`). Wire-format compatibility preserved 1:1.
  - **Display boundary**: `fmtBaht` / `formatBahtCurrency` take Double —
    unwrap with `.amount` at the call site
    (e.g. `fmtBaht(sale.total.amount)`). Same for `Quantity` when calling
    APIs that want raw Int (`drug.stock.value`).
  - **Predicates**: `money.isPositive` / `money.isZero` /
    `qty.isPositive` replace `> 0` / `== 0.0` / `<= 0` comparisons.
    Arithmetic operators (`+ - * /` and `coerceAtLeast` / `coerceAtMost`)
    are defined on `Money` (multiply by `Int` keeps `Money`; divide by
    `Int` keeps `Money`).
  - **Aggregation**:
    `items.fold(Money.Zero) { acc, line -> acc + line.lineTotal }`
    (stays in `Money` space) instead of
    `items.sumOf { it.lineTotal.amount }` (unwraps eagerly).
  - **Defaults**: `Money.Zero` / `Quantity.Zero` instead of `0.0` / `0`
    on constructor defaults so the type doesn't degrade.
  - **Form-input boundary**: VM fields stay `String` (users type into
    text fields). Conversion happens at submit:
    `Money(field.toDoubleOrNull() ?: 0.0)` /
    `Quantity(field.toIntOrNull() ?: 0)`.
  - **Still `Double` (intentional)**: `ReportSummary` / `DailySales` /
    `MonthlySales` (`todaySales` / `monthSales` / `stockValue` /
    `total`), receipt template wire fields (`ReceiptTemplate.total` /
    `subtotal` / `change` — printer protocol). Migrating those is a
    separate RepX arc.

- **Datetime / timezone (Asia/Bangkok)**: the backend stores `time.Time`
  in MongoDB → JSON-serializes as RFC3339 with `Z` (UTC) or
  `+00:00` / `+07:00` offset. The KMP app displays in Bangkok local
  time. Use the existing helpers — they handle the conversion:
  - `String?.parseLocalDateTimeOrNull()`
    (`:core:data/data/internal/DateConv.kt`) parses ISO datetime,
    converts UTC → `Asia/Bangkok` `LocalDateTime` when the input has a
    `Z` / offset marker; passes through naked datetimes (assumed
    already-local) for round-trip safety. Naked input
    `2026-05-17T14:42:00Z` → `LocalDateTime(2026, 5, 17, 21, 42)`.
  - `String?.parseLocalDateOrNull()` accepts both `YYYY-MM-DD` and a
    full datetime string (`2027-06-30T00:00:00Z` →
    `LocalDate(2027, 6, 30)`), falling back to `take(10)`. Backend
    sends date-only fields (`Drug.ExpiryDate`, lot `expiry_date`,
    `import_date`) as `time.Time` serialized to RFC3339 — without this
    fallback the strict `LocalDate.parse` rejects them and the UI
    shows blank.
  - `isoDateTimeToBuddhist(s)` / `localDateTimeToBuddhist(dt)` /
    `isoDateToBuddhist(s)` / `localDateToBuddhist(d)`
    (`:core:ui/ui/format/DateFormat.kt`) format for Thai display
    (`DD/MM/YYYY+543` with optional time). Same UTC→Bangkok /
    datetime-as-date conversions inside.
  - **M3 `DatePicker` round-trip uses UTC** (per its public contract).
    `ymdToMillis(ymd)` / `millisToYmd(ms)` /
    `LocalDate.toStartOfDayMillis()` use `TimeZone.UTC` internally
    regardless of the `tz` parameter passed (param kept for source
    compat with `@Suppress("UNUSED_PARAMETER")`). A YMD string
    round-trips through M3 without an off-by-one day shift.
  - Default `TimeZone` everywhere is `TimeZone.of("Asia/Bangkok")` —
    `DEFAULT_ZONE` in `DateFormat.kt`, `BANGKOK` in `DateConv.kt`,
    `FALLBACK` in `:core:domain/observer/TimeZoneProvider.kt`.
    Outbound `LocalDate.toIso()` / `LocalDateTime.toIso()` emit the
    plain ISO form (no Z / no offset) — round-trip with the parsers
    above preserves the value for already-local datetimes.

- **Responsive layout**: one codebase spans 320px phone → desktop. Breakpoints:
  320 (floor, nothing below) / 360 (stack `Row→Column`) / 600 (`WindowSize.Compact`
  ↔ `Medium`; `PharmTable` switches to card mode) / 720 (`MetricCardRow` 4-up) /
  840 (`Medium` ↔ `Expanded`). `WindowSize.fromWidth(maxWidth)` lives in
  `:core:ui/ui/components/WindowSize.kt`. Use `BoxWithConstraints` + `FlowRow`
  for content reflow; `PharmTable` columns take `hideInCompact` / `compactTitle`.
  Desktop/web floor at 600px (`Main.kt` `window.minimumSize`, `index.html`
  `min-width`). Collect state with `collectAsStateWithLifecycle()` (not
  `collectAsState`) everywhere — battery.
- **Adding a new feature** → follow the 6-step recipe above (see
  `MODULE_GRAPH.md` for full graph + adding-new-code quick-lookup table).
- **Skills**: project-specific skills live in `.claude/skills/` —
  `pharmacy-kmp-feature` (scaffold), `pharmacy-kmp-add-form` (`BaseFormViewModel`),
  `pharmacy-kmp-test` (`runVmTest` + fakes), `pharmacy-kmp-screen-split`
  (Screen↔Content + responsive), `pharmacy-kmp-review` (audit a diff against the
  10 build-enforced rules + conventions). Reach for them before hand-rolling
  these flows.

## Cross-cutting reminders

See repo-root `CLAUDE.md` at `/Users/admin/ProjectPos/CLAUDE.md` for
workspace-wide conventions (auth flow, role hierarchy, Thai-first copy,
typo-preserving package names in Go services, etc.).
