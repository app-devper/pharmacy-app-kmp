---
name: compose-multiplatform-patterns
description: General-purpose patterns for building shared UI across Android, iOS, Desktop, and Web using Compose Multiplatform and Jetpack Compose. Covers state management (ViewModel + StateFlow + collectAsStateWithLifecycle), navigation (type-safe @Serializable routes), reusable composable design (slot-based APIs), KMP platform-specific UI (interface + Koin OR expect/actual), performance optimization (stable types, derivedStateOf, lazy list keys, recomposition avoidance), and Material 3 theming. Use when building Compose UI, managing UI state with ViewModels and Compose state, implementing navigation in KMP or Android projects, designing reusable composables and design systems, or optimizing recomposition and rendering performance. Triggers on "Compose state management", "compose navigation", "compose pattern", "recomposition performance", "stable composable", "slot API", "expect actual composable", "Material 3 theme".
metadata:
  author: worawit
  version: "1.1"
---

# Compose Multiplatform Patterns

Patterns for building shared UI across Android, iOS, Desktop, and Web using Compose Multiplatform
and Jetpack Compose. Covers state management, navigation, theming, and performance.

> **Companions**: see `.claude/skills/kmp-*` for strict-KMP project conventions
> (`kmp-code-pattern`, `kmp-layout-pattern`, `kmp-design-system`, `kmp-navigation`, `kmp-platform`)
> when working in a project that adopts those rules.

## When to Activate

- Building Compose UI (Jetpack Compose or Compose Multiplatform)
- Managing UI state with ViewModels and Compose state
- Implementing navigation in KMP or Android projects
- Designing reusable composables and design systems
- Optimizing recomposition and rendering performance

---

## State Management

### ViewModel + Single State Object

Use a single data class for screen state. Expose it as `StateFlow` and collect in Compose:

```kotlin
data class ItemListState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class ItemListViewModel(
    private val getItems: GetItemsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ItemListState())
    val state: StateFlow<ItemListState> = _state.asStateFlow()

    fun onSearch(query: String) {
        _state.update { it.copy(searchQuery = query) }
        loadItems(query)
    }

    private fun loadItems(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getItems(query).fold(
                onSuccess = { items -> _state.update { it.copy(items = items, isLoading = false) } },
                onFailure = { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
            )
        }
    }
}
```

### Collecting State in Compose

```kotlin
@Composable
fun ItemListScreen(viewModel: ItemListViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ItemListContent(
        state = state,
        onSearch = viewModel::onSearch
    )
}

@Composable
private fun ItemListContent(
    state: ItemListState,
    onSearch: (String) -> Unit
) {
    // Stateless composable — easy to preview and test
}
```

### Event Sink Pattern

For complex screens, use a sealed interface for events instead of multiple callback lambdas:

```kotlin
sealed interface ItemListEvent {
    data class Search(val query: String) : ItemListEvent
    data class Delete(val itemId: String) : ItemListEvent
    data object Refresh : ItemListEvent
}

// In ViewModel
fun onEvent(event: ItemListEvent) {
    when (event) {
        is ItemListEvent.Search -> onSearch(event.query)
        is ItemListEvent.Delete -> deleteItem(event.itemId)
        is ItemListEvent.Refresh -> loadItems(_state.value.searchQuery)
    }
}

// In Composable — single lambda instead of many
ItemListContent(
    state = state,
    onEvent = viewModel::onEvent
)
```

> An alternative — used by the `kmp-code-pattern` skill set — is a `<Feature>Callbacks` data
> class of lambdas with no-op defaults, passed to `<Feature>Content(state, callbacks)`. Both
> patterns are valid: sink is more uniform; callbacks-data-class makes `@Preview` trivial
> (`Callbacks()` constructs all no-ops) and gives the IDE direct call-site references.

---

## Navigation

### Type-Safe Navigation (Compose Navigation 2.8+)

Define routes as `@Serializable` objects:

```kotlin
@Serializable data object HomeRoute
@Serializable data class DetailRoute(val id: String)
@Serializable data object SettingsRoute

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(onNavigateToDetail = { id -> navController.navigate(DetailRoute(id)) })
        }
        composable<DetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<DetailRoute>()
            DetailScreen(id = route.id)
        }
        composable<SettingsRoute> { SettingsScreen() }
    }
}
```

**Gotcha**: a module that declares `@Serializable` route objects must apply the
`kotlin-serialization` Gradle plugin in its `build.gradle.kts` — otherwise the build is green
but you get `SerializationException: Serializer for class '<Route>' is not found` at runtime.

### Dialog and Bottom Sheet Navigation

Use `dialog()` and overlay patterns instead of imperative show/hide:

```kotlin
NavHost(navController, startDestination = HomeRoute) {
    composable<HomeRoute> { /* ... */ }
    dialog<ConfirmDeleteRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ConfirmDeleteRoute>()
        ConfirmDeleteDialog(
            itemId = route.itemId,
            onConfirm = { navController.popBackStack() },
            onDismiss = { navController.popBackStack() }
        )
    }
}
```

> For app shells with a sidebar/topbar that should persist across feature navigation, see
> `.claude/skills/kmp-navigation/` — it documents a two-level `NavHost` with the shell
> composed once around a nested `NavHost` of per-feature routes.

---

## Composable Design

### Slot-Based APIs

Design composables with slot parameters for flexibility:

```kotlin
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Card(modifier = modifier) {
        Column {
            header()
            Column(content = content)
            Row(horizontalArrangement = Arrangement.End, content = actions)
        }
    }
}
```

### Modifier Ordering

Modifier order matters — apply in this sequence:

```kotlin
Text(
    text = "Hello",
    modifier = Modifier
        .padding(16.dp)                  // 1. Layout (padding, size)
        .clip(RoundedCornerShape(8.dp))  // 2. Shape
        .background(Color.White)         // 3. Drawing (background, border)
        .clickable { }                   // 4. Interaction
)
```

---

## Platform-specific UI (KMP)

There are two ways to express "different per platform" in Compose Multiplatform. Pick one per
project; mixing them is a source of drift.

### Option A — `expect`/`actual` for platform Composables

The language-level mechanism. Works well for small libraries or apps where every module already
has `androidMain`/`iosMain` source folders:

```kotlin
// commonMain
@Composable expect fun PlatformStatusBar(darkIcons: Boolean)

// androidMain
@Composable
actual fun PlatformStatusBar(darkIcons: Boolean) {
    val systemUiController = rememberSystemUiController()
    SideEffect { systemUiController.setStatusBarColor(Color.Transparent, darkIcons) }
}

// iosMain
@Composable
actual fun PlatformStatusBar(darkIcons: Boolean) {
    // iOS handles this via UIKit interop or Info.plist
}
```

### Option B — Interface + Koin (no `expect`/`actual`)

The DI-level mechanism. Required for projects whose audit task bans `expect`/`actual` and
restricts platform folders to a single entry-point module:

```kotlin
// commonMain — interface (no @Composable surface)
interface StatusBarController {
    fun setLightIcons(darkIcons: Boolean)
}

// composeApp/androidMain
class StatusBarControllerImpl(private val systemUiController: SystemUiController) : StatusBarController {
    override fun setLightIcons(darkIcons: Boolean) {
        systemUiController.setStatusBarColor(Color.Transparent, darkIcons)
    }
}

// in commonMain composable: pull the binding via Koin
@Composable
fun PlatformStatusBar(darkIcons: Boolean) {
    val controller: StatusBarController = koinInject()
    SideEffect { controller.setLightIcons(darkIcons) }
}
```

**When to prefer B**:
- The project has a strict "platform folders only in `:composeApp`" rule (audit-enforced).
- You want fakeable contracts for tests (`FakeStatusBarController : StatusBarController`).
- You want `Find Usages` on the interface to reliably surface every impl.

See `.claude/skills/kmp-platform/` for the full pattern with `FileDownloader`,
`ReceiptPrinter`, `HttpClient` engine, `AppDispatchers`, and `Settings`.

---

## Performance

### Stable Types for Skippable Recomposition

Mark classes as `@Stable` or `@Immutable` when all properties are stable:

```kotlin
@Immutable
data class ItemUiModel(
    val id: String,
    val title: String,
    val description: String,
    val progress: Float
)
```

### Use `key()` and Lazy Lists Correctly

```kotlin
LazyColumn {
    items(
        items = items,
        key = { it.id }  // Stable keys enable item reuse and animations
    ) { item ->
        ItemRow(item = item)
    }
}
```

### Defer Reads with `derivedStateOf`

```kotlin
val listState = rememberLazyListState()
val showScrollToTop by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 5 }
}
```

### Avoid Allocations in Recomposition

```kotlin
// BAD — new lambda and list every recomposition
items.filter { it.isActive }.forEach { ActiveItem(it, onClick = { handle(it) }) }

// GOOD — key each item so callbacks stay attached to the right row
val activeItems = remember(items) { items.filter { it.isActive } }
activeItems.forEach { item ->
    key(item.id) {
        ActiveItem(item, onClick = { handle(item) })
    }
}
```

---

## Theming

### Material 3 Dynamic Theming

```kotlin
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
```

> Projects with a hand-curated brand palette typically **disable** dynamic theming (it would
> override the brand colors) and read tokens from a custom `BrandTokens` data class via a
> `CompositionLocal`. See `.claude/skills/kmp-design-system/` for the token + primitives
> approach.

---

## Anti-Patterns to Avoid

- Using `mutableStateOf` in ViewModels when `MutableStateFlow` with `collectAsStateWithLifecycle` is safer for lifecycle
- Passing `NavController` deep into composables — pass lambda callbacks instead
- Heavy computation inside `@Composable` functions — move to ViewModel or `remember {}`
- Using `LaunchedEffect(Unit)` as a substitute for ViewModel init — it re-runs on configuration change in some setups
- Creating new object instances in composable parameters — causes unnecessary recomposition
- Forgetting the `kotlin-serialization` plugin on a module that declares `@Serializable` routes (silent runtime crash)

---

## Project-specific deviations (strict KMP-style projects)

Opinionated KMP projects often tighten or invert the general patterns above. When a project's
`CLAUDE.md` adopts the **`kmp-*` skill set** (`.claude/skills/kmp-code-pattern`,
`kmp-layout-pattern`, `kmp-design-system`, `kmp-navigation`, `kmp-platform`,
`kmp-error-handling`), follow the project's rules when they conflict with the general patterns.

| General pattern | Strict-KMP project rule | Why / reference |
|---|---|---|
| `class FooViewModel : ViewModel()` + raw `_state = MutableStateFlow(...)` + manual `viewModelScope.launch { try { ... } catch (e) { ... } }` | All VMs extend `BaseViewModel<S>(initial)` and use `setState { copy(...) }` + `launchResult(block = { useCase() }, onSuccess, onFailure)`. Form VMs extend `BaseFormViewModel<S>` over F-bounded `BaseFormUiState<S>` and override `persist(): Result<Unit>` only. | See `.claude/skills/kmp-code-pattern/` and `.claude/skills/kmp-add-form/`. Eliminates boilerplate; consistent error lifecycle (`error: String?` + `dismissError()`); `launchResult` already handles `runCatching` + `CancellationException` rethrow. |
| `expect fun PlatformX()` / `actual fun PlatformX()` for platform-bound composables and APIs | **Banned**: NO `expect class` / `expect fun` / `expect val` anywhere. Define an interface in `:core:common`, implement in `:composeApp/<plat>Main/platform/X*Impl.kt`, bind per-platform via Koin in each `Main*.kt`. The audit task fails the build on any `expect` declaration. | See `.claude/skills/kmp-platform/`. The expect/actual seam is repeatedly the friction point — interface + impl + Koin binding makes the platform contract explicit + testable + DI-substitutable. |
| `MaterialTheme(colorScheme = dynamicLightColorScheme(...))` | Use `<Brand>Theme { ... }` which provides `LocalBrandTokens` (a `BrandTokens(colors, spacing, radii, shapes, dimens)` data class) — production code reads `brandTokens.colors.accent` not `MaterialTheme.colorScheme.primary`. **No raw M3 widgets in new files**: use `<Brand>Button` / `<Brand>Badge` / `<Brand>TextField` / `FormField` / `<Brand>Modal` / `MetricCard` / `<Brand>Table` / `<Brand>FilterChips` / `<Brand>ActionMenu` / `<Brand>TabBar` / `<Brand>StatusBadge` / `<Brand>DateRangeField` etc. from the project's `:core:ui/.../designsystem/`. | See `.claude/skills/kmp-design-system/`. The design follows a hand-curated palette; dynamic Material You theming would override and clash with it. |
| `class FooViewModel(private val repo: FooRepository)` | VM constructor injects only `<X>UseCase`s + `<X>Provider`s (read-only singletons) + buses — never the Repository directly. Repository → use case → VM. | See `.claude/skills/kmp-code-pattern/` §3. Keeps business logic out of the VM; use cases are independently testable; Providers give an ISP narrow read-only surface. |
| `sealed interface FooEvent + onEvent(event)` sink | Project uses explicit setters (`onSearch(q)`, `onDelete(id)`, `onRefresh()`) bundled in a `<Feature>Callbacks` data class with default no-ops, passed to `<Feature>Content(state, callbacks)`. | See `.claude/skills/kmp-code-pattern/` §2. Makes `@Preview` trivial (`Callbacks()` constructs all no-ops); IDE call-site references are direct; one less indirection through `when`. Both patterns are valid — strict-KMP picks the callbacks-data-class shape. |
| `viewModelScope.launch(Dispatchers.IO) { ... }` | Use cases own their own IO switch via `BaseUseCase(dispatchers: AppDispatchers)` + `withContext(dispatchers.io)`. VM constructor does NOT take `dispatchers` or `logger` at all. `viewModelScope.launch { ... }` only — no dispatcher arg ever. | See `.claude/skills/kmp-code-pattern/` and `.claude/skills/kmp-test/`. Single source of truth for IO offload; VM stays on Main; tests use `runVmTest { dispatchers -> }` to inject a test-scheduler-backed `AppDispatchers`. |
| `class FooRepositoryImpl { suspend fun get(): Result<Foo> = runCatching { ... } }` | Repository interfaces return bare `T` (no `Result`); impls throw typed `AppException` subclasses (`AuthException` / `ForbiddenException` / `NotFoundException` / `ConflictException` / `NetworkException` / `ServerException` / `ValidationException` / `StorageException` / `UnsupportedPlatformException`). `BaseUseCase` wraps the call once in `runCatching` and converts to `Result<R>`. Ktor `HttpResponseValidator` translates HTTP status → typed `AppException` automatically. | See `.claude/skills/kmp-error-handling/` and `.claude/skills/kmp-data-layer/`. Repos stay bare; use cases own the error policy; no double-wrapping. |
| `Screen.kt` + `Content.kt` + `ViewModel.kt` + `UiState.kt` may live in the same file when small | **File-per-class**: each must live in its own file. An audit grep for `class .*ViewModel|fun .*Screen\(|fun .*Content\(|class .*UiState` in the same file fails the build. | See `.claude/skills/kmp-code-pattern/` §2. |
| Page header is custom per screen (Topbar / Toolbar / breadcrumb) | Every page is `Column { BrandListToolbar(title, subtitle, onBack?, actions?) ; weighted content column }`. Sub-pages differ ONLY by passing `onBack` to the toolbar. No `Scaffold`. No `PageScaffold` wrapper. | See `.claude/skills/kmp-layout-pattern/`. |

For the full set of strict-KMP rules: see `.claude/skills/kmp-rules/` (cross-cutting
conventions), `.claude/skills/kmp-feature/` (vertical-slice scaffold),
`.claude/skills/kmp-review/` (audit checks), `.claude/skills/kmp-add-form/` (form recipe),
`.claude/skills/kmp-screen-split/` (Content split recipe),
`.claude/skills/kmp-build-logic/` (convention plugins + the audit Gradle task).

When in doubt: **the project's `CLAUDE.md` supersedes the general patterns above** when working
in that codebase. Outside such projects, the general patterns apply as written.
