---
name: kmp-layout-pattern
description: The unified page layout for a Compose Multiplatform app — every screen is Column { BrandListToolbar ; content }; sub-pages differ ONLY by passing onBack; responsive breakpoints and Screen ↔ Content split. Use when starting a new screen, auditing for layout drift, or refactoring scaffolds.
---

# kmp-layout-pattern

The layout pattern is **one** structure for every page; sub-pages just pass `onBack`. No
`PageScaffold` / `SubPageScaffold` / `DetailScaffold` abstractions — the toolbar primitive carries
the difference.

## 1. The single pattern (memorize this exactly)

```kotlin
Column(
    modifier = Modifier.fillMaxSize().background(brandTokens.colors.bgPage),
) {
    BrandListToolbar(
        title = "Title",
        subtitle = "Optional subtitle",
        onBack = null,                                   // ← sub-pages pass a lambda; list pages pass null
        searchValue = …, onSearchChange = …,             // optional
        filters = { … },                                 // optional FlowRow of chips / pickers
        actions = { … },                                 // optional Row of trailing controls
    )
    Column(
        modifier = Modifier
            .weight(1f)                                  // ← always weight(1f), never fillMaxSize, so content doesn't overflow the toolbar
            .fillMaxWidth()
            [.verticalScroll(rememberScrollState())]     // forms only
            .padding(16.dp),                             // page gutter
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // page content here
    }
}
```

### Why only one pattern

- **Sub-pages = list pages + `onBack`**. The `BrandListToolbar` primitive renders a back arrow
  when `onBack != null` and forces the title to show at all widths.
- **No centered max-width.** Forms are full-width like lists; if you start centering them, drift
  reappears within weeks.
- **`weight(1f)` is non-negotiable.** `fillMaxSize` inside the column lets content slip behind
  the flush toolbar. Reviewers should flag any sub-page using `fillMaxSize` here.

## 2. Screen ↔ Content split (file-per-class)

Every page is two files:

```kotlin
// <X>Screen.kt — stateful, ~10 lines
@Composable
fun ThingsScreen(vm: ThingsViewModel = koinViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()              // NEVER collectAsState — battery
    ReloadOnResume(vm::reload)                                       // list/dashboard pages only
    ThingsContent(
        state = state,
        callbacks = ThingsCallbacks(
            onReload = vm::reload,
            onRowClick = vm::onRowClick,
            onDismissError = vm::dismissError,
        ),
    )
}

// <X>Content.kt — stateless, previewable
@Composable
fun ThingsContent(state: ThingsUiState, callbacks: ThingsCallbacks = ThingsCallbacks()) {
    // the Column { BrandListToolbar ; content } pattern above
    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Preview @Composable private fun ThingsContent_Loaded_Preview() {
    BrandTheme { ThingsContent(state = ThingsUiState(rows = sampleRows)) }
}
@Preview @Composable private fun ThingsContent_Loading_Preview() {
    BrandTheme { ThingsContent(state = ThingsUiState(loading = true)) }
}
@Preview @Composable private fun ThingsContent_Empty_Preview() {
    BrandTheme { ThingsContent(state = ThingsUiState()) }
}
```

Rules:
- Content takes `(state, callbacks)` only. No `koinViewModel`/`koinInject`, no `LaunchedEffect`,
  no `viewModelScope`. It must compile in a `@Preview` with no backend.
- Callbacks is a `data class` of lambdas, each defaulted to `{}` so previews render without
  wiring everything.
- Screen handles `collectAsStateWithLifecycle` + `ReloadOnResume(vm::reload)` (list/dashboard
  only — so a record added on a detail page appears on resume) and wires `Callbacks(...)`.

## 3. Building blocks

| Block | When | Notes |
|---|---|---|
| `BrandListToolbar` | every page | `onBack=null` = list page, `onBack=lambda` = sub-page |
| `BrandListResultLine(total, noun, visible?, searching?, trailing?)` | every list page | "ทั้งหมด N <noun>" under the toolbar |
| `BrandListSkeleton` | loading state | rectangle pulses, count from a sensible default |
| `BrandEmptyState(icon, title, subtitle?)` | empty state | semantic — not just a `Text("ว่าง")` |
| `ErrorBottomSheet(message, onDismiss)` | every page | renders `state.error: String?` |
| `BrandTable(rows, columns, key)` | list pages | responsive: card mode <600dp, h-scroll on overflow |
| `BrandListCard(title, subtitle?, status?, body?, trailing?)` | list pages (alternative to table when items are entity-card shaped) | offline-sync style list |
| `BrandFormCard(title, subtitle?) { … }` | every form section | sub-pages place these in the content column |
| `BrandSaveAction(saving, canSubmit, onSubmit, label?)` | form sub-pages | goes in the toolbar's `actions` slot — never a bottom save bar |
| `BrandMetricCard / MetricCardRow` | dashboard metrics | 4-up ≥720dp via FlowRow |

The table card is wrapped in a surface card **inside** the padded content column — not around
the whole page. So:

```kotlin
Column(modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp)) {
    BrandListResultLine(total = state.things.size, noun = "รายการ")
    Column(
        modifier = Modifier
            .weight(1f).fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
    ) {
        when {
            state.loading -> BrandListSkeleton()
            state.things.isEmpty() -> BrandEmptyState(icon = BrandIcons.Empty, title = "ว่างเปล่า")
            else -> BrandTable(rows = state.things, columns = columns, key = { it.id })
        }
    }
}
```

## 4. Sub-pages

A sub-page (detail / form / history) is the same `Column { BrandListToolbar ; content }` with:
- `onBack = { navController.popBackStack() }` passed through from the screen
- form sub-pages: put `BrandSaveAction(...)` in the toolbar `actions` slot; the body is a
  `verticalScroll` column of `BrandFormCard(title) { fields }` sections
- detail sub-pages: the body can be a `LazyColumn` if it's a long stream of cards; otherwise the
  same padded column

```kotlin
// Form sub-page wiring (inside Content):
Column(Modifier.fillMaxSize().background(t.colors.bgPage)) {
    BrandListToolbar(
        title = state.titleLabel,                         // "เพิ่ม X" / "แก้ไข X"
        onBack = callbacks.onBack,
        actions = {
            BrandSaveAction(
                saving = state.saving,
                canSubmit = state.canSubmit,
                onSubmit = callbacks.onSubmit,
            )
        },
    )
    Column(
        modifier = Modifier
            .weight(1f).fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BrandFormCard(title = "Section A") { … }
        BrandFormCard(title = "Section B") { … }
    }
}
```

No `PharmSubPage`/`PageScaffold` wrapper, no bottom save bar, no inline Cancel button (back arrow
is the way out), no centered max-width.

## 5. Responsive breakpoints

| Width | Meaning | Helper |
|---|---|---|
| `< 320dp` | not supported | — (set `window.minimumSize` / `min-width: 320px`) |
| `< 360dp` | tightest phone | `BoxWithConstraints { if (maxWidth < 360.dp) Column else Row }` |
| `< 600dp` | **Compact** (mobile) | `WindowSize.Compact`; `BrandTable` auto-renders **card mode** |
| `600–840dp` | **Medium** (small tablet / desktop floor) | `WindowSize.Medium` |
| `≥ 840dp` | **Expanded** | `WindowSize.Expanded` |
| `≥ 720dp` | metric cards go 4-up | handled inside `MetricCardRow` |

- Use `WindowSize.fromWidth(maxWidth)` for shell-level decisions (sidebar collapse).
- Use `BoxWithConstraints { maxWidth … }` + `FlowRow` for content-level reflow; stack
  `Row`→`Column` below ~360–700dp rather than letting weighted children crush.
- `BrandTable` already switches to card mode `<600dp` and horizontal-scrolls when columns overflow
  — set `hideInCompact = true` on low-priority columns and `compactTitle = true` on the primary one.
- Desktop floors at 600px (`Main.kt` `window.minimumSize`); web floors via `index.html`
  `min-width: 600px`. Don't design inner screens that assume `<600dp` on desktop/web.

## 6. List resume reload

Every list/dashboard `Screen.kt` calls:

```kotlin
ReloadOnResume(vm::reload)
```

…so a record added on a detail page reflects when the user navigates back. (For filter-driven
VMs use `applyFilter` / `loadList`.) Missing this on a list page is a real bug — the user adds an
item, comes back, and the new row is missing until they pull-to-refresh.

## 7. Anti-patterns to flag in review

- Toolbar **nested inside** a surface card (old style) instead of flush at top.
- Using `Modifier.fillMaxSize` instead of `Modifier.weight(1f)` on the content column → overlaps the toolbar.
- A sub-page rendering its own back arrow / breadcrumb instead of passing `onBack` to the toolbar.
- A form with a bottom save bar or inline Cancel button.
- A form centering itself with `widthIn(max = …).fillMaxWidth()` while a list page next to it is full-width.
- A list/dashboard `Screen.kt` missing `ReloadOnResume(vm::reload)` (stale UI after add/edit).
- `collectAsState()` instead of `collectAsStateWithLifecycle()` (battery drain).
- A `@Composable` doing `koinInject()` on a UseCase or Repository (MVVM violation).
- A net-new screen using raw Material 3 widgets instead of `Brand*` primitives.
- Per-screen hex colors / dp constants instead of `brandTokens`.

## 8. Verify

```bash
./gradlew :composeApp:compileTestKotlinWasmJs :features:<feat>:jvmTest
./gradlew :composeApp:run                  # desktop; resize across 320 / 600 / 720 / 1100 to eyeball reflow
```
