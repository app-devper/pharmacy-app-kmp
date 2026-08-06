---
name: kmp-screen-split
description: Refactor a fat Compose screen in a Compose Multiplatform project into the canonical Screen ↔ Content + Callbacks + @Preview split, including responsive layout via BoxWithConstraints/WindowSize. Use when a screen mixes state-collection with rendering, lacks previews, or needs responsive behavior.
---

# kmp-screen-split

Every screen splits into a **stateful `Screen`** (Koin + state collection) and a **stateless
`Content`** (pure, previewable). One file per concept — no `Screen`+`Content` in the same file.

**No comments. `Brand*` primitives only** (no raw Material 3 in net-new). See **kmp-design-system**
for the primitive list, **kmp-layout-pattern** for the page structure.

## Target shape

```kotlin
// <X>Screen.kt — stateful
@Composable
fun ThingsScreen(viewModel: ThingsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ReloadOnResume(viewModel::reload)                    // list/dashboard pages only
    ThingsContent(
        state = state,
        callbacks = ThingsCallbacks(
            onReload = viewModel::reload,
            onRowClick = viewModel::onRowClick,
            onDismissError = viewModel::dismissError,
        ),
    )
}

// <X>Content.kt — stateless, previewable
@Composable
fun ThingsContent(state: ThingsUiState, callbacks: ThingsCallbacks = ThingsCallbacks()) {
    val t = brandTokens
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        BrandListToolbar(title = "Things", subtitle = "…", actions = { … })
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // loading / empty / data
        }
    }
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

## Refactor steps

1. **Extract `Content`** — move all rendering into `<X>Content(state, callbacks)`. It takes no
   VM, does no `koinInject`/`koinViewModel`, launches no coroutines, and must compile in a
   `@Preview` with no backend.
2. **Create `<X>Callbacks`** — a `data class` of lambdas, every one defaulted to `{}`. Replace
   direct `viewModel::foo` calls inside the body with `callbacks.foo`.
3. **Slim `Screen`** — keep only `koinViewModel()`, `collectAsStateWithLifecycle()` (NEVER
   `collectAsState` — battery), and the `Callbacks(...)` wiring.
   - For **list/dashboard screens** also call `ReloadOnResume(vm::reload)` so adding/editing on a
     child page reflects when the user navigates back. Use whichever public refetch fn the VM
     exposes (`reload`, or `applyFilter` / `loadList` for filter-driven VMs).
4. **Add ≥3 `@Preview`** — at least loaded / loading / empty using `BrandTheme { … }` and local
   sample data (`private val sampleRows = …`). Previews must compile without a backend.
5. **Strip comments** as part of the edit; rename unclear locals instead of commenting.
6. **Split files** — if `Screen` and `Content` were in the same file, extract `Content` to its
   own `<X>Content.kt`. Same for `UiState`/`ViewModel`/`Callbacks` if any of them shared a file
   (the file-per-class rule).

## Layout pattern

Use the single unified pattern from **kmp-layout-pattern**: `Column { BrandListToolbar ; weighted
content column }`. Sub-pages differ only by passing `onBack` to the toolbar — there is **no**
separate `SubPageScaffold` / `DetailScaffold` abstraction.

Anti-patterns to fix during the split:
- Toolbar nested inside the table surface card → toolbar must be flush at the top of the page.
- Content column using `fillMaxSize` instead of `weight(1f)` → content slips behind the toolbar.
- Form centering itself with `widthIn(max = …)` while neighbouring list pages are full-width.
- A sub-page rendering its own back arrow / breadcrumb instead of using `BrandListToolbar(onBack)`.
- Form with a bottom save bar instead of `BrandSaveAction` in the toolbar `actions` slot.

## Responsive layout

Match these breakpoints when splitting layout:

| Width | Meaning | Helper |
|---|---|---|
| `< 320dp` | not supported | floor enforced by `window.minimumSize` / `min-width: 320px` |
| `< 360dp` | tightest phone | `BoxWithConstraints { if (maxWidth < 360.dp) Column else Row }` |
| `< 600dp` | **Compact** (mobile) | `WindowSize.Compact`; `BrandTable` auto-renders **card mode** |
| `600–840dp` | **Medium** | `WindowSize.Medium` |
| `≥ 840dp` | **Expanded** | `WindowSize.Expanded` |
| `≥ 720dp` | metric cards go 4-up | inside `MetricCardRow` |

- Use `WindowSize.fromWidth(maxWidth)` for shell-level decisions.
- Use `BoxWithConstraints { maxWidth … }` + `FlowRow` (wraps) for content-level reflow; stack
  `Row`→`Column` below ~360–700dp rather than letting weighted children crush.
- `BrandTable` already switches to card mode `< 600dp` and h-scrolls when columns overflow — set
  `hideInCompact = true` on low-priority columns and `compactTitle = true` on the primary one.
- Desktop window floors at 600px (`Main.kt` `window.minimumSize`); web floors via `index.html`
  `min-width: 600px`. Don't design inner screens that assume `< 600dp` on desktop/web.

## Verify

```bash
./gradlew :features:<feat>:jvmTest :composeApp:compileTestKotlinWasmJs
./gradlew :composeApp:run                  # desktop; resize to eyeball reflow at 320 / 600 / 720 / 1100
```
