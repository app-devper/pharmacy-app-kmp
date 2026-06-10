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
  Quick smoke: `./gradlew :composeApp:check`. Current test count: **960
  `@Test` functions across 126 commonTest files** (re-measure with
  `grep -rn '@Test' core features composeApp --include='*.kt' | wc -l`).

- **Coverage (Kover)**: `./gradlew koverVerify` enforces a **line-coverage
  floor** (`COVERAGE_FLOOR` in root `build.gradle.kts`, currently **50%** —
  a ratchet to raise over time toward the 80% target, not a one-shot gate;
  measured ~55% today). `koverHtmlReport` → `build/reports/kover/html/`.
  Filters exclude UI composables (`@Composable`, `*Screen`/`*Content`),
  the i18n string tables (`i18n.groups`), `ui.print`, and DTOs — the
  measured layers are domain / use-case / VM / mappers / localizers. CI
  runs `koverVerify` after the test sweep; **raise the floor in the same PR
  whenever you add tests that push coverage up.**

- **Forbidden imports** (enforced by `auditArchitecture` — A10 / A17 /
  A19 / A20 / A23 / A24 / A25 / A26 / A27 / A28 / A29). Full rule list in
  [MODULE_GRAPH.md § Forbidden imports](./MODULE_GRAPH.md#forbidden-imports-p0).
  Headline: `:core:*` ห้าม import จาก `:features:*`; `:features:<x>` ห้าม
  import จาก `:core:data` หรือ feature `<y>` อื่น; platform source folders
  อยู่ใน `:composeApp` เท่านั้น; ห้าม `expect`/`actual` ที่ไหนเลย; ห้าม
  throw generic exceptions in production; ห้าม Thai string literal ใน
  production UI code (A29 — ทุก copy ต้องผ่าน `PharmStrings`).

- **Typed errors (A28) — end-to-end, no String errors anywhere**:
  production code throws typed `AppException` subclasses (abstract base in
  `:core:common`; transport subclasses Auth / Forbidden / NotFound /
  Conflict / Network / Server / Validation / Storage /
  UnsupportedPlatform), never raw `IllegalStateException` /
  `RuntimeException` / `Exception`. `BaseUseCase.invoke()` wraps via
  `runCatching` → `Result<R>`. `:features:test-fixtures` is exempted.
  - **VM error state is typed**: every UiState carries
    `errorState: AppException?` (+ `domainError` override on
    `BaseUiState`); there is NO `error: String?` field anywhere. Info
    toasts use a parallel plain-sealed `messageState`
    (`CommonUiStateMessage.{Saved, ExportEmpty}` or feature-specific).
  - **Feature errors**: per-UiState sealed classes in
    `presentation/<x>/exception/` (e.g.
    `SalesHistoryUiStateError.LoadBillsFailed(cause)`), wrapping the
    cause for logs. Generic ops reuse
    `CommonUiStateError.{Load,Save,Delete,Export}Failed`
    (`:core:common/error/`).
  - **Localization happens at render, never in the VM**: each feature
    ships `presentation/<x>/i18n/<X>ErrorLocalize.kt` —
    `fun AppException.localize<X>(s: PharmStrings)` mapping its own
    cases then delegating `else -> localizeCommon(s)`
    (`:core:ui/i18n/CommonErrorLocalize.kt` — covers CommonUiStateError,
    all 9 transport types, `FieldValidationError`, and passes
    `ValidationException.message` through verbatim because those
    messages are domain-authored and user-facing). Render:
    `ErrorBottomSheet(message = state.errorState?.localize<X>(pharmStrings))`.
  - **Form base** (`BaseFormViewModel`): `submit()` routes failures
    through `mapSaveError(cause)` — passes `AppException`s through,
    wraps unknowns in `CommonUiStateError.SaveFailed`; state mutates via
    `withDomainError(AppException?)` on `BaseFormUiState`. Loadable base
    (`BaseLoadableViewModel`) only provides `dismissError()`.
  - **Domain validation is structured, not stringly**:
    `:core:domain/validation/` — `FieldValidationError(field: FieldLabel,
    rule)` thrown by `Field.*` validators, plus `SaleValidationError`
    (EmptyCart / Return* / VoidReasonRequired) and `BulkImportParseError`
    (EmptyInput / NotArrayOrObject / RowNotObject(row) / RowMissingName).
    `:core:ui` localizes them (`ValidationStrings` group composes
    rule × field label).

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
  - **Date picking is `PharmDatePicker`** (`:core:ui/designsystem/` —
    custom calendar; M3 `DatePicker` is fully removed). It keeps M3's
    **UTC-millis contract**: `ymdToMillis` / `millisToYmd` /
    `utcMillisToLocalDate` / `LocalDate.toUtcStartOfDayMillis` use
    `TimeZone.UTC`, so YMD strings round-trip without off-by-one shifts.
    Month/weekday names come from the `CalendarStrings` group
    (live-localized); the Thai header renders Buddhist era
    ('มิถุนายน 2569'), English renders CE ('June 2026'). Grid logic is
    pure (`CalendarMonth.weeks()`, Sunday-first) and unit-tested.
    'Today' resolves in Asia/Bangkok.
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

- **Localization — COMPLETE & build-enforced** (default ภาษาไทย, English
  switchable live; **A29** fails the build on any new Thai literal in
  production UI code).
  - **The system is the Kotlin-typed string table** —
    `:core:ui/.../i18n/PharmStrings.kt`: a composite interface built by
    **delegation over ~25 per-module group interfaces**
    (`i18n/groups/<G>Strings.kt` + `Th`/`En` objects; ~990 keys). Read in
    Composables via `pharmStrings.<key>` (a `@ReadOnlyComposable` getter
    on `LocalPharmStrings`); interpolated copy uses lambda keys
    (`sellKyIncomplete: (String, String) -> String`). `App {}` wraps
    content in `AppLocaleProvider(localeWire = uiPreferences.locale.wire)`
    so the Profile 'ภาษา' chip recomposes everything instantly — no
    restart for anything, including the calendar (`PharmDatePicker`).
    (Background: `compose.resources` couldn't live-switch because CMP
    1.11's `LocalComposeEnvironment` is package-private; the
    `strings.xml` files + per-platform cold-start locale bootstrap in
    each `Main*.kt` remain only as a safety net for minor M3 built-ins
    that read `Locale.current`.)
  - **Locale storage**: `UiPreferences.locale: LocalePreference = System`
    (System/Th/En), persisted via `UiPreferencesRepository.setLocale(...)`
    → `SetLocalePreferenceUseCase`. Picker UI: `ProfileScreen` → 'ภาษา'
    chip group.
  - **Adding copy** (the only supported path): add the key to the right
    group interface + `Th` + `En` objects (feature keys in
    `<Feature>Strings.kt`, shared in `CommonStrings.kt`), then read via
    `pharmStrings`. Never hardcode — A29 will fail the build.
  - **Composable-scope recipes** (the patterns that earlier sweeps broke
    on): `semantics {}` / `remember {}` / `LaunchedEffect` bodies can't
    call `pharmStrings` → capture `val s = pharmStrings` at composable
    scope first (and key caches with it: `remember(s) { ... }` so tables
    rebuild on locale switch); non-composable helpers take an
    `s: PharmStrings` parameter; enum display labels are `label(s)` /
    `localizedLabel(s)` extension functions, never a `label: String`
    field on the enum; default parameter values of `@Composable`
    functions MAY call `pharmStrings` (defaults evaluate in
    composition).
  - **Intentionally still Thai** (A29-exempt): the `PharmStringsTh`
    table itself, `@Preview` sample data and `private val sample*/preview*`
    blocks, `.contains(...)` data-matching tokens, stored-data defaults
    (e.g. `unit = "ชิ้น"`), printed receipts (`ui/print/` — customer
    documents), bulk-import example JSON, and KY official form copy in
    `:core:domain` reports.

## Cross-cutting reminders

See repo-root `CLAUDE.md` at `/Users/admin/ProjectPos/CLAUDE.md` for
workspace-wide conventions (auth flow, role hierarchy, Thai-first copy,
typo-preserving package names in Go services).
