---
name: pharmacy-kmp-screen-split
description: Refactor a fat Compose screen in the pharmacy-app KMP companion into the canonical Screen ↔ Content + Callbacks + @Preview split, including responsive layout via BoxWithConstraints/WindowSize. Use when a screen mixes state-collection with rendering, lacks previews, or needs responsive behavior in /Users/admin/ProjectPos/pharmacy-app/app-kmp.
---

# pharmacy-kmp-screen-split

Every screen splits into a **stateful `Screen`** (Koin + state collection) and a
**stateless `Content`** (pure, previewable). **No comments. `Pharm*` primitives only.**

## Target shape

```kotlin
@Composable
fun StockScreen(viewModel: StockViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StockContent(
        state = state,
        callbacks = StockCallbacks(
            onReload = viewModel::reload,
            onRowClick = viewModel::onRowClick,
            onDismissError = viewModel::dismissError,
        ),
    )
}

@Composable
fun StockContent(state: StockUiState, callbacks: StockCallbacks = StockCallbacks()) {
    val t = pharmTokens
    // loading / empty / data using Pharm* primitives
    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Preview @Composable private fun StockContent_Loaded_Preview() {
    PharmacyTheme { StockContent(state = StockUiState(rows = sampleRows)) }
}
```

## Refactor steps
1. **Extract `Content`** — move all rendering into `<X>Content(state, callbacks)`. It must take
   no VM, do no `koinInject`/`koinViewModel`, launch no coroutines.
2. **Create `<X>Callbacks`** — a `data class` of lambdas, every one defaulted to `{}`. Replace
   direct `viewModel::foo` calls inside the body with `callbacks.foo`.
3. **Slim `Screen`** — keep only `koinViewModel()`, `collectAsStateWithLifecycle()`, and the
   `Callbacks(...)` wiring. **Always `collectAsStateWithLifecycle`** (not `collectAsState`) for battery.
   For **list/dashboard screens** also call `ReloadOnResume(vm::reload)` (from
   `:core:ui/ui/common/`) so adding/editing on a child page reflects when the user navigates
   back. Use the public refetch fn the VM exposes (`reload`, or `applyFilter` / `loadList`
   for filter-driven VMs).
4. **Add `@Preview`** — at least loaded / loading / empty using `PharmacyTheme { … }` and local
   sample data (`private val sampleRows = …`). Previews must compile without a backend.
5. **Strip comments** as part of the edit; rename unclear locals instead of commenting.

## Responsive layout
The design system is responsive — match these breakpoints when splitting layout:

| Width | Meaning | Helper |
|---|---|---|
| `< 320dp` | not supported below | — |
| `< 360dp` | tightest phone | `BoxWithConstraints { if (maxWidth < 360.dp) Column else Row }` |
| `< 600dp` | **Compact** (mobile) | `WindowSize.Compact`; `PharmTable` auto-renders **card mode** |
| `600–840dp` | **Medium** (small tablet / desktop floor) | `WindowSize.Medium` |
| `≥ 840dp` | **Expanded** | `WindowSize.Expanded` |
| `≥ 720dp` | metric cards go 4-up | handled inside `MetricCardRow` |

- Use `WindowSize.fromWidth(maxWidth)` (in `:core:ui`, `ui/components/WindowSize.kt`) for shell-level decisions.
- Use `BoxWithConstraints { maxWidth … }` + `FlowRow` (wraps) for content-level reflow; stack
  `Row`→`Column` below ~360–700dp rather than letting weighted children crush.
- `PharmTable` already switches to card mode `< 600dp` and horizontal-scrolls when columns don't
  fit — set `hideInCompact = true` on low-priority columns and `compactTitle = true` on the primary one.
- Desktop window floors at 600px (`Main.kt` `window.minimumSize`); web floors via `index.html`
  `min-width: 600px`. Don't design inner screens that assume `< 600dp` on desktop/web.

## Verify
```bash
./gradlew :features:<feat>:jvmTest :composeApp:compileTestKotlinWasmJs
```
Run `:composeApp:run` (desktop) and resize to eyeball reflow at 320 / 600 / 720 / 1100.
