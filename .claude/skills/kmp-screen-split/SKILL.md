---
name: kmp-screen-split
description: Refactor a fat Compose screen in the pharmacy app into the canonical Screen ↔ Content + Callbacks + @Preview split. Use when a screen mixes state-collection with rendering, lacks previews, or has grown past ~300 lines.
---

# kmp-screen-split

Every screen splits into a **stateful `Screen`** (Koin + state collection) and a
**stateless `Content`** (pure, previewable). One file per concept — never
`Screen` and `Content` in the same file.

No comments anywhere. `Pharm*` primitives only. See `kmp-design-system` for the
primitive list and `kmp-layout-pattern` for the page structure this produces.

## Target shape

```kotlin
// <X>Screen.kt — stateful
@Composable
fun StockScreen(viewModel: StockViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ReloadOnResume(viewModel::reload)                    // list/dashboard pages only
    StockContent(
        state = state,
        callbacks = StockCallbacks(
            onReload = viewModel::reload,
            onSearch = viewModel::onSearch,
            onDismissError = viewModel::dismissError,
        ),
    )
}

// <X>Content.kt — stateless, previewable
@Composable
fun StockContent(state: StockUiState, callbacks: StockCallbacks = StockCallbacks()) {
    val s = pharmStrings
    PharmListScaffold(
        toolbar = { StockToolbar(state = state, callbacks = callbacks) },
        resultLine = { PharmListResultLine(total = state.drugs.size, noun = s.stockCountNoun) },
    ) {
        when {
            state.loading && state.drugs.isEmpty() -> PharmListSkeleton()
            state.errorState != null && state.drugs.isEmpty() -> PharmErrorState()
            state.drugs.isEmpty() -> PharmEmptyState(icon = PharmIcons.Stock, title = s.stockListEmpty)
            else -> StockTable(drugs = state.filtered, callbacks = callbacks)
        }
    }
    ErrorBottomSheet(
        message = state.errorState.unlessPageShowsError(state.drugs.isEmpty())?.localizeStock(s),
        onDismiss = callbacks.onDismissError,
    )
}

@Preview @Composable private fun StockContent_Loaded_Preview() {
    PharmacyTheme { StockContent(state = StockUiState(drugs = sampleDrugs)) }
}
@Preview @Composable private fun StockContent_Loading_Preview() {
    PharmacyTheme { StockContent(state = StockUiState(loading = true)) }
}
@Preview @Composable private fun StockContent_Empty_Preview() {
    PharmacyTheme { StockContent(state = StockUiState()) }
}
```

## Refactor steps

1. **Extract `Content`** — move all rendering into `<X>Content(state, callbacks)`.
   It takes no ViewModel, does no `koinInject` / `koinViewModel`, launches no
   coroutines, and must compile in a `@Preview` with no backend.
2. **Create `<X>Callbacks`** — a `data class` of lambdas, every one defaulted to
   `{}` (or a `Preview` companion instance where the callbacks are non-optional,
   as `UsersListCallbacks.Preview` does). Replace `viewModel::foo` in the body
   with `callbacks.foo`.
3. **Slim `Screen`** — only `koinViewModel()`, `collectAsStateWithLifecycle()`
   (never `collectAsState` — battery), `ReloadOnResume(vm::reload)` on
   list/dashboard pages, and the `Callbacks(...)` wiring.
4. **Split further when the Content passes ~300 lines.** The convention is
   sibling stateless composables in the same package: `<X>Toolbar.kt`,
   `<X>Table.kt`, `<X>MetricsRow.kt`, `<X>Card.kt`. Forms go one level down in
   a `form/` package.
5. **Add ≥3 `@Preview`** — loaded / loading / empty, wrapped in
   `PharmacyTheme { … }` with local `private val sample*` data. Sample data may
   contain Thai literals; A29 skips everything from the first `@Preview` or
   `private val sample*` marker onward.
6. **Strip comments** as part of the edit; rename unclear locals instead.

## What the split must fix on the way through

- Typed errors: `state.errorState: AppException?`, never `error: String?`.
  Localize at render with `localize<X>(pharmStrings)` — never in the ViewModel.
- Copy comes from `pharmStrings`. In `remember {}` / `semantics {}` /
  `LaunchedEffect` bodies you cannot call `pharmStrings`, so capture
  `val s = pharmStrings` at composable scope and key caches with it
  (`remember(s) { columns() }`) so tables rebuild on locale switch.
- A list page hand-rolling `Column { toolbar ; surface card }` becomes
  `PharmListScaffold`.
- A sub-page content column using `fillMaxSize()` becomes `weight(1f)`.
- A sub-page drawing its own back arrow passes `onBack` to `PharmListToolbar`.
- A form with a bottom save bar moves to `PharmSaveAction` in the toolbar
  `actions` slot, and picks up `pharmFormContentWidth()` +
  `pharmFormContentPadding()`.
- Money/Quantity: display unwraps at the call site (`fmtBaht(x.amount)`,
  `qty.value`); the state keeps the value classes.

## Responsive

Two named tiers — `isCompactShell` (< 840dp, chrome) and `isCompactContent`
(< 600dp, data density). Never compare the raw `WindowSize` enum. Full table
and the platform floors are in `kmp-layout-pattern` §5.

## Verify

```bash
./gradlew :features:<feat>:jvmTest :composeApp:compileTestKotlinWasmJs
```

Then actually look at it — the `run` skill drives the wasm build in headless
Edge and screenshots it at whatever widths you ask for.
