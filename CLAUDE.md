# CLAUDE.md — pharmacy-app KMP companion

Project-scoped instructions for Claude Code in
`/Users/admin/ProjectPos/pharmacy-app/app-kmp/` — a Compose Multiplatform
app shipped to Android / iOS / JVM / wasmJs.

**26 Gradle modules**: `:composeApp` (entry, only module with platform
source folders) + `:core:{common,domain,ui,data}` + `:features:test-fixtures`
+ 20 `:features:<x>` (auth, bulkimport, customers, expiry, help, imports,
ky, labels, movements, offlinesync, planning, profile, reports, saleshistory,
sell, settings, stock, stockcount, suppliers, users).

Full structural reference — what lives where, dependency matrix, forbidden
imports, per-feature recipe, test layout, migration history — in
[MODULE_GRAPH.md](./MODULE_GRAPH.md).

## Navigation (two-level NavHost + single shell)

`:composeApp` owns navigation. `AppNavHost` is the OUTER `NavHost(startDestination = Login)`
with two destinations: **`authNav`** (Login, no shell) and
**`composable<MainRoot> { MainShell(...) }`**. `LaunchedEffect(isLoggedIn)`
swaps between them.

`MainShell` (`composeApp/.../navigation/MainNav.kt`) renders `AppShell`
(sidebar/topbar) **once** around a nested `NavHost` that composes every
`<x>Nav` builder. Sub-pages (forms/details) live under the same shell —
there is no per-feature `ShelledScreen`.

- Active sidebar item + topbar title come from `nestedNav.currentBackStackEntryAsState()`
  via the `DEST_INFO` map (`route qualified name → (title, sectionKey)`).
  Sub-pages map to their parent section so the sidebar stays highlighted.
- Each feature exposes `fun NavGraphBuilder.<x>Nav(...)` in
  `presentation/<x>/navigation/<X>Nav.kt`. Leaf features take no params;
  features with sub-pages take `navController`; cross-feature jumps take
  a hoisted `() -> Unit` callback (only `stockNav`'s `onOpenReorderSuggestions`
  today).

## Code style — NO COMMENTS

ห้ามใส่ comment ใด ๆ ลงในไฟล์ `.kt` — `//`, `/* */`, `/** */` (KDoc),
TODO / FIXME / NOTE / HACK markers, file-header banners. Code ต้อง
self-documenting ผ่านชื่อ + types + small functions. ถ้ารู้สึกว่าต้องอธิบาย
อะไร → rename หรือ refactor แทน.

Applies to production + tests + fakes + helpers (incl. `:features:test-fixtures`).
When restyling existing code, **strip surrounding comments as part of
the edit**.

**Exceptions** (annotations, not comments): `@Suppress("...")` annotations,
license headers required by upstream libraries.

## Project reminders

- **Stack**: Kotlin Multiplatform 2.3.21 / Compose Multiplatform 1.11.0 /
  AGP 8.13.2 / Gradle 8.14.3. Targets: `jvm`, `android`, `iosArm64`,
  `iosSimulatorArm64`, `wasmJs`. JDK 17+.

- **Convention plugins** (`build-logic/`): every KMP library applies one
  of `id("pharmacy.kmp.library")` (pure data/domain) or
  `id("pharmacy.kmp.compose.library")` (compose-aware — `:core:ui` + all
  20 `:features:<x>`). `:composeApp` adds `pharmacy.architecture.audit`
  for the `auditArchitecture` task.

- **Test verify** (canonical sweep):
  ```bash
  ./gradlew :composeApp:auditArchitecture :composeApp:testDebugUnitTest \
            :composeApp:compileTestKotlinIosSimulatorArm64 \
            :composeApp:compileTestKotlinWasmJs \
            :core:{common,domain,ui,data}:jvmTest \
            :features:{auth,bulkimport,customers,expiry,help,imports,ky,labels,movements,offlinesync,planning,profile,reports,saleshistory,sell,settings,stock,stockcount,suppliers,users}:jvmTest
  ```
  Quick smoke: `./gradlew :composeApp:check`. Current test count: **759
  `@Test` functions across 111 commonTest files** (re-measure with
  `grep -rn '@Test' core features composeApp --include='*.kt' | wc -l`).

- **Forbidden imports** (enforced by `auditArchitecture` — A10 / A17 /
  A19 / A20 / A23 / A24 / A25 / A26 / A27 / A28). Full rule list in
  [MODULE_GRAPH.md § Forbidden imports](./MODULE_GRAPH.md#forbidden-imports-p0).
  Headline: `:core:*` ห้าม import จาก `:features:*`; `:features:<x>` ห้าม
  import จาก `:core:data` หรือ feature `<y>` อื่น; platform source folders
  อยู่ใน `:composeApp` เท่านั้น; ห้าม `expect`/`actual` ที่ไหนเลย; ห้าม
  throw generic exceptions in production.

- **Typed errors (A28)**: production code throws typed `AppException`
  subclasses (Auth / Forbidden / NotFound / Conflict / Network / Server /
  Validation / Storage / UnsupportedPlatform), never raw
  `IllegalStateException` / `RuntimeException` / `Exception`.
  `BaseUseCase.invoke()` wraps via `runCatching` → `Result<R>`.
  `:features:test-fixtures` is exempted (fakes throw `RuntimeException`
  as a deliberate test signal).

- **Money / Quantity value classes** (`:core:common/value/`): every
  monetary field on a domain model / param is `Money` (wraps `Double`),
  every counted-stock field is `Quantity` (wraps `Int`). End-to-end
  through `Drug` / `AltUnit` / `Sale*` / `CartLine` / `CartDiscount` /
  `CartSnapshot` / `ParkedCart` / `SellUiState` / `CheckoutParam` /
  `DrugLot` / `PurchaseOrder*` / `ReorderSuggestion`.
  - DTOs stay Double/Int (wire format unchanged). Mappers wrap inbound
    (`Money(dto.x)` / `Quantity(dto.y)`), unwrap outbound (`x.amount` /
    `y.value`).
  - Display: unwrap with `.amount` at the call site
    (`fmtBaht(sale.total.amount)`); `qty.value` for raw Int APIs.
  - Predicates: `money.isPositive` / `qty.isPositive` / `isZero` replace
    `> 0` / `== 0.0`. Operators `+ - * /` + `coerceAtLeast/AtMost`.
  - Aggregation:
    `items.fold(Money.Zero) { acc, x -> acc + x.lineTotal }` (Money space)
    over `items.sumOf { it.lineTotal.amount }` (unwraps eagerly).
  - Defaults: `Money.Zero` / `Quantity.Zero`, never `0.0` / `0`.
  - Form-input boundary: VM fields stay `String`, wrap at submit
    (`Money(field.toDoubleOrNull() ?: 0.0)`).
  - Still `Double` (intentional): `ReportSummary` / `DailySales` /
    `MonthlySales`; receipt template wire fields (printer protocol).

- **Datetime / timezone (Asia/Bangkok)**: backend stores `time.Time` in
  Mongo → RFC3339 with `Z` (UTC) or offset; KMP displays Bangkok local.
  - `String?.parseLocalDateTimeOrNull()` (`:core:data/data/internal/DateConv.kt`)
    converts UTC → Bangkok when offset marker present; passes through
    naked datetimes for round-trip safety.
  - `String?.parseLocalDateOrNull()` accepts both `YYYY-MM-DD` and full
    datetime (`2027-06-30T00:00:00Z` → `LocalDate(2027,6,30)` via
    `take(10)` fallback — backend sends `time.Time` for date-only fields
    like `expiry_date`).
  - `isoDateTimeToBuddhist` / `localDateTimeToBuddhist` /
    `isoDateToBuddhist` / `localDateToBuddhist`
    (`:core:ui/ui/format/DateFormat.kt`) for Thai display
    (`DD/MM/YYYY+543`).
  - **M3 `DatePicker` uses UTC** per its public contract. `ymdToMillis` /
    `millisToYmd` / `LocalDate.toStartOfDayMillis` use `TimeZone.UTC`
    internally regardless of the `tz` param. YMD strings round-trip
    through M3 without an off-by-one day shift.
  - Default `TimeZone` is `TimeZone.of("Asia/Bangkok")` (`DEFAULT_ZONE` /
    `BANGKOK` / `FALLBACK` constants).

- **DTO field convention** (A24 + A25): every `@Serializable data class`
  property in `:core:data/.../remote/dto/` and `data/storage/*Dto.kt`:
  1. camelCase Kotlin name (`val sellPrice: Double`).
  2. Explicit `@SerialName("wire_name")` even when wire matches Kotlin.
  Single line: `@SerialName("sell_price") val sellPrice: Double = 0.0,`.

- **Design system**: tokens in `:core:ui/ui/theme/DesignTokens.kt`;
  primitives in `ui/designsystem/Pharm*.kt`. **No M3 widgets** in net-new
  files — use `PharmButton` / `PharmBadge` / `PharmTextField` / `FormField` /
  `PharmModal` / `MetricCard` / `DrugCard` / `PharmTable` / `PharmStaticTable` /
  `PharmFilterChips` / `PharmIcons` (32 SVG vectors).

- **Page scaffold primitives** (every screen looks the same):
  - `PharmListToolbar(title, subtitle, onBack, searchValue, filters, actions)`
    — the page-header for BOTH list pages and sub-pages. Pass `onBack`
    to render a back button (sub-page mode); title/subtitle show even
    below 600dp width.
  - `PharmListResultLine(total, noun, visible, searching, trailing)` —
    "ทั้งหมด N <noun>" band; `trailing` slot for per-page totals.
  - **Sub-pages** wrap in
    `Column(Modifier.fillMaxSize().background(bgPage)) { PharmListToolbar(..., onBack); <content> }`.
    Content is `Column(Modifier.weight(1f).fillMaxWidth()[.verticalScroll(...)].padding(...))`
    — full-width like main pages.
  - `PharmFormCard(title, subtitle)` — form section card (rounded surface
    + 1dp border + h2 title + 16dp inner spacing).
  - `PharmSaveAction(saving, canSubmit, onSubmit, label)` — save control
    in the sub-page toolbar's `actions` slot. Forms no longer ship a
    bottom save bar.
  - `ReloadOnResume(onResume)` (`ui/common/`) — `LifecycleEventObserver`
    that calls `viewModel::reload` on `ON_RESUME`. Every list/dashboard
    uses it.

- **Responsive layout**: 320px floor → desktop. Breakpoints: 320 / 360
  (stack `Row→Column`) / 600 (`WindowSize.Compact ↔ Medium`; `PharmTable`
  switches to card mode) / 720 (`MetricCardRow` 4-up) / 840 (`Medium ↔
  Expanded`). `WindowSize.fromWidth(maxWidth)` in
  `:core:ui/ui/components/WindowSize.kt`. Use `BoxWithConstraints` +
  `FlowRow`; `PharmTable` columns take `hideInCompact` / `compactTitle`.
  Desktop/web floor at 600px (`Main.kt window.minimumSize`, `index.html
  min-width`). Always `collectAsStateWithLifecycle()` (battery).

- **Adding a new feature** → 6-step recipe in
  [MODULE_GRAPH.md § Per-feature recipe](./MODULE_GRAPH.md#per-feature-recipe).

- **Skills** (`.claude/skills/`) — reach for these before hand-rolling:
  `pharmacy-kmp-feature` (scaffold), `pharmacy-kmp-add-form`
  (`BaseFormViewModel`), `pharmacy-kmp-test` (`runVmTest` + fakes),
  `pharmacy-kmp-screen-split` (Screen↔Content + responsive),
  `pharmacy-kmp-review` (audit a diff against the build-enforced rules).

## Cross-cutting reminders

See repo-root `CLAUDE.md` at `/Users/admin/ProjectPos/CLAUDE.md` for
workspace-wide conventions (auth flow, role hierarchy, Thai-first copy,
typo-preserving package names in Go services).
