---
name: kmp-layout-pattern
description: The unified page layout for the pharmacy app — list pages are PharmListScaffold, sub-pages are Column { PharmListToolbar(onBack) ; content }, plus the Screen ↔ Content split, the gutter/width rules and the two responsive tiers. Use when starting a screen, auditing for layout drift, or refactoring a scaffold.
---

# kmp-layout-pattern

Two shapes, and only two. A list page is a `PharmListScaffold`; anything you
reach *from* a list page is a `Column { PharmListToolbar(onBack = …) ; content }`.
There is no `PageScaffold` / `DetailScaffold` / `ShelledScreen` — the shell is
rendered once by `MainShell` (see `kmp-navigation`), and the toolbar primitive
carries the list-vs-sub-page difference.

## 1. List pages — `PharmListScaffold`

```kotlin
PharmListScaffold(
    toolbar = { PharmListToolbar(subtitle = s.stockSubtitle, searchValue = …, actions = { … }) },
    metrics = { StockMetricsRow(state) },            // optional — collapses on scroll when compact
    banner  = { … },                                 // optional
    resultLine = { PharmListResultLine(total = state.drugs.size, noun = s.stockCountNoun) },
    footer = { … },                                  // optional, pinned below the workspace
) {
    when {
        state.loading && state.drugs.isEmpty() -> PharmListSkeleton()
        state.errorState != null && state.drugs.isEmpty() -> PharmErrorState()
        state.drugs.isEmpty() -> PharmEmptyState(icon = PharmIcons.Stock, title = s.stockListEmpty)
        else -> StockTable(drugs = visible, callbacks = callbacks)
    }
}
```

The scaffold owns everything you would otherwise re-derive per page:

- caps the workspace at `dimens.listWorkspaceMaxWidth` (768dp) and centres it
- applies `pharmPageGutter` (16dp compact / 24dp above) horizontally
- draws the surface card + 1dp border around the body **above** 600dp and drops
  the frame below it (`isCompactContent`), so phones get edge-to-edge rows
- collapses the metrics band on nested scroll when it is showing stat pills

The `content` lambda is `ColumnScope` and already sits in a `weight(1f)` column
— never wrap it in another `fillMaxSize()`.

**Four states, in this order.** Skeleton → error → empty → data. Getting the
error branch wrong is how a 503 ends up reading as "no records yet"; pair it
with `state.errorState.unlessPageShowsError(rows.isEmpty())` on the
`ErrorBottomSheet` so the failure is stated once and survives dismissal.

## 2. Sub-pages — forms, details, history

```kotlin
Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
    PharmListToolbar(
        title = state.titleLabel,
        onBack = callbacks.onBack,                   // ← this is what makes it a sub-page
        actions = {
            PharmSaveAction(                          // forms only
                saving = state.saving,
                canSubmit = state.canSubmit,
                onSubmit = callbacks.onSubmit,
            )
        },
    )
    Column(
        modifier = Modifier
            .weight(1f)                               // ← never fillMaxSize, or content slides under the toolbar
            .then(pharmFormContentWidth())            // centres + caps at formContentMaxWidth (768dp)
            .verticalScroll(rememberScrollState())    // forms and long details
            .pharmFormContentPadding(),               // the same gutter the toolbar uses
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PharmFormCard(title = s.sectionGeneral) { … }
        PharmFormCard(title = s.sectionPricing) { … }
    }
}
```

- `onBack` renders the back arrow and forces the title to show at every width.
  A sub-page never draws its own back arrow or breadcrumb.
- Save lives in the toolbar `actions` slot. No bottom save bar, no inline
  Cancel — the back arrow is the way out.
- **Content is width-capped and centred**, not full-bleed: 768dp for lists and
  forms, 880dp for long-form help (`readingContentMaxWidth`), 1040dp for the
  reports dashboard (`dashboardContentMaxWidth`).

## 3. Screen ↔ Content split (file-per-class)

```kotlin
// <X>Screen.kt — stateful, ~10 lines
@Composable
fun StockScreen(vm: StockViewModel = koinViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()   // never collectAsState — battery
    ReloadOnResume(vm::reload)                            // list/dashboard pages only
    StockContent(
        state = state,
        callbacks = StockCallbacks(
            onReload = vm::reload,
            onSearch = vm::onSearch,
            onDismissError = vm::dismissError,
        ),
    )
}

// <X>Content.kt — stateless, previewable
@Composable
fun StockContent(state: StockUiState, callbacks: StockCallbacks = StockCallbacks()) {
    // the PharmListScaffold above
    ErrorBottomSheet(
        message = state.errorState.unlessPageShowsError(state.drugs.isEmpty())?.localizeStock(pharmStrings),
        onDismiss = callbacks.onDismissError,
    )
}

@Preview @Composable private fun StockContent_Loaded_Preview() {
    PharmacyTheme { StockContent(state = StockUiState(drugs = sampleDrugs)) }
}
```

- Content takes `(state, callbacks)` only. No `koinViewModel`, no
  `LaunchedEffect`, no `viewModelScope` — it must render in a `@Preview` with
  no backend.
- `Callbacks` is a `data class` of lambdas each defaulted to `{}`.
- Errors are typed: `state.errorState: AppException?`, localized **at render**
  via `localize<X>(pharmStrings)`. There is no `error: String?` anywhere.
- Big pages split further — `<X>Toolbar.kt`, `<X>Table.kt`, `<X>MetricsRow.kt`
  as siblings, all still stateless.

## 4. Building blocks

| Block | When |
|---|---|
| `PharmListScaffold` | every list/dashboard page |
| `PharmListToolbar` | every page — `onBack = null` list, `onBack = {}` sub-page |
| `PharmListResultLine(total, noun, visible?, searching?, trailing?)` | every list page |
| `PharmListSkeleton` / `PharmErrorState` / `PharmEmptyState` | the three non-data states |
| `ErrorBottomSheet(message, onDismiss)` | every page |
| `PharmTable(rows, columns, key)` | tabular lists — card mode below 600dp |
| `PharmListCard` | list rows that are entity-card shaped rather than tabular |
| `PharmFormCard(title, subtitle?)` | every form section |
| `PharmSaveAction(saving, canSubmit, onSubmit)` | form sub-pages, in the toolbar |
| `MetricCard` / `MetricCardRow` | dashboard metrics — 4-up above 720dp |
| `ReloadOnResume(vm::reload)` | every list/dashboard `Screen.kt` |

## 5. Responsive — two tiers, and each component says which it means

| Helper | Threshold | Drives |
|---|---|---|
| `WindowSize.isCompactShell` | `< 840dp` (`!= Expanded`) | chrome: sidebar → drawer, compact topbar, metrics → stat pills, toolbar collapses |
| `WindowSize.isCompactContent` | `< 600dp` (`== Compact`) | data density: `PharmTable` → cards, action menus → bottom sheets, modals full-screen, list surface drops its frame |

Never compare the raw enum — the named tier says which question is being asked.
`PharmBreakpoint` holds the numbers: `Stack` 360, `FormTwoCol` 560,
`Medium` 600, `FormThreeCol` 720, `Expanded` 840, `GridWide` 1280.

- `WindowSize.fromWidth(maxWidth)` inside `BoxWithConstraints` for local
  decisions; `LocalWindowSize.current` for the shell-level one.
- `FlowRow` + stacking `Row → Column` below ~360dp rather than letting
  weighted children crush.
- `PharmTable` columns take `hideInCompact` (drop when the width won't fit) and
  `compactTitle` / `compactTrailing` (what leads the card in card mode).
- **Platform floors differ on purpose**: desktop will not go below 600×600
  (`Main.kt` `window.minimumSize`), web runs down to 320px (`index.html`
  `min-width`), Android/iOS are whatever the device is. Only web and mobile
  ever reach the compact tier.

## 6. Anti-patterns to flag in review

- A list page hand-rolling `Column { toolbar ; card }` instead of `PharmListScaffold`.
- `Modifier.fillMaxSize()` on the content column of a sub-page (use `weight(1f)`).
- A sub-page drawing its own back arrow instead of passing `onBack`.
- A form with a bottom save bar or an inline Cancel button.
- A page hardcoding `16.dp` horizontally instead of `pharmPageGutter` / `pharmFormContentPadding()`.
- A list missing the error branch, so a failed load renders the empty state.
- `state.error: String?` or localizing inside the ViewModel.
- A list/dashboard `Screen.kt` missing `ReloadOnResume(vm::reload)`.
- `collectAsState()` instead of `collectAsStateWithLifecycle()`.
- A `@Composable` calling `koinInject()` on a UseCase or Repository.
- Net-new raw Material 3 widgets, or per-screen hex/dp constants instead of `pharmTokens`.

## 7. Verify

```bash
./gradlew :composeApp:compileTestKotlinWasmJs :features:<feat>:jvmTest
```

Then look at it. The `run` skill builds the wasm bundle, serves it with the
mock API and drives it in headless Edge — resize across 320 / 600 / 720 / 1100
and read the screenshots. Type checks verify code, not layout.
