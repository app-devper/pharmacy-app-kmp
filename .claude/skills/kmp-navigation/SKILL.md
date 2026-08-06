---
name: kmp-navigation
description: The two-level navigation + single-shell pattern for a Compose Multiplatform app — AppNavHost composes AuthNav + MainShell; MainShell renders the AppShell ONCE and hosts a nested NavHost that composes every FeatureNav; routes live per-feature; cross-feature jumps go through hoisted () -> Unit callbacks. Use when designing app navigation, adding a screen, or wiring a deep link.
---

# kmp-navigation

The navigation pattern is **two-level NavHost with a single shell**. The app shell (sidebar /
topbar) is composed **once** at the top of `MainShell`, and every feature lives inside a nested
NavHost below it. There is no per-feature `ShelledScreen` wrapper.

```
AppNavHost (outer NavHost, startDestination = Login)
  ├── composable<Login>     { LoginScreen(onLoggedIn = …) }       ← AuthNav
  └── composable<MainRoot>  { MainShell(appViewModel) }
            │
            └── MainShell renders AppShell(sidebar, topbar) ONCE
                  └── nested NavHost(startDestination = Sell)
                        ├── sellNav(nestedNav)            ← FeatureNav 1
                        ├── stockNav(nestedNav, onOpenReorderSuggestions = …)
                        ├── customersNav(nestedNav)
                        ├── … every other feature
                        └── profileNav(nestedNav)
```

## Why two-level + single shell

- **Performance**: the shell renders once. Switching sections does NOT re-create the sidebar /
  topbar / nav controller / its state. Massive composition savings vs. wrapping each
  destination.
- **Consistency**: the shell is one composable — it can't drift between features.
- **Cross-feature navigation**: `MainShell` sees every route, so cross-feature jumps work
  without features importing each other.
- **Auth gate**: a top-level `LaunchedEffect(isLoggedIn)` swaps `Login` ↔ `MainRoot`, clearing
  the back stack — exactly once, not per-feature.

## 1. `AppNavHost` (composeApp)

```kotlin
@Serializable data object MainRoot

@Composable
fun AppNavHost(viewModel: AppViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val rootNav = rememberNavController()

    LaunchedEffect(state.isLoggedIn) {
        rootNav.navigate(if (state.isLoggedIn) MainRoot else Login) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(rootNav, startDestination = Login) {
        authNav(
            onLoggedIn = {
                rootNav.navigate(MainRoot) {
                    popUpTo(Login) { inclusive = true }
                }
            },
        )
        composable<MainRoot> { MainShell(appViewModel = viewModel) }
    }
}
```

The outer NavHost has exactly two destinations. **`authNav`** is a `NavGraphBuilder` extension
(it just contributes the `Login` composable). **`MainRoot`** is a single composable that hosts
the shell + nested host.

## 2. `MainShell` (composeApp/navigation/MainNav.kt)

```kotlin
@Composable
fun MainShell(appViewModel: AppViewModel) {
    val state by appViewModel.state.collectAsStateWithLifecycle()
    val nestedNav = rememberNavController()
    val backEntry by nestedNav.currentBackStackEntryAsState()
    val info = destInfoFor(backEntry?.destination?.route)         // central route → (title, sectionKey)

    AppShell(
        title = info.title,
        items = mainNavItems,                                     // computed from MAIN_NAV_TABLE
        currentRoute = info.sectionKey ?: "",                     // sub-pages map to their parent section
        onNavigate = { key ->
            if (key != info.sectionKey) {
                routeForKey(key)?.let { route ->
                    nestedNav.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(Sell) { saveState = true }
                    }
                }
            }
        },
        onLogout = appViewModel::signOut,
        pendingSyncCount = state.pendingSyncCount,
        role = state.role,
        user = topbarUser(state),
        onProfileClick = { nestedNav.navigate(Profile) { launchSingleTop = true } },
    ) {
        NavHost(navController = nestedNav, startDestination = Sell) {
            sellNav(nestedNav)
            stockNav(
                nestedNav,
                onOpenReorderSuggestions = { nestedNav.navigate(ReorderSuggestions) { launchSingleTop = true } },
            )
            customersNav(nestedNav)
            // … every other feature
            profileNav(nestedNav)
        }
    }
}
```

Key pieces:
- **`mainNavItems`** is a static `List<NavItem>` mapped from a `MAIN_NAV_TABLE: List<MainNavEntry>`
  in the same file. The shell only knows about `NavItem` (id, label, icon, admin). The table
  knows the route objects.
- **`destInfoFor(route: String?)`** is a `Map<String, DestInfo>` lookup keyed by
  `route::class.qualifiedName`. It returns `(title, sectionKey)`:
  - `title` → what the topbar shows
  - `sectionKey` → which sidebar item is highlighted (sub-pages map to their parent section's
    key; orphans return `null`)
- The same map covers list pages AND sub-pages — write **one entry per destination**, not one
  per section.

## 3. Per-feature `<X>Nav.kt` (file-per-class)

Each feature owns its routes + a single `NavGraphBuilder` extension:

```kotlin
// features/customers/src/commonMain/.../presentation/customers/navigation/CustomersNav.kt
@Serializable data object Customers
@Serializable data object CustomerAdd
@Serializable data class CustomerEdit(val id: String)
@Serializable data class CustomerDetail(val id: String)

fun NavGraphBuilder.customersNav(navController: NavController) {
    composable<Customers> {
        CustomersScreen(
            onAddCustomer = { navController.navigate(CustomerAdd) { launchSingleTop = true } },
            onOpenCustomer = { id -> navController.navigate(CustomerDetail(id)) { launchSingleTop = true } },
            onEditCustomer = { id -> navController.navigate(CustomerEdit(id)) { launchSingleTop = true } },
        )
    }
    composable<CustomerAdd> { CustomerFormScreen(customerId = null, onBack = { navController.popBackStack() }) }
    composable<CustomerEdit> { entry ->
        val route = entry.toRoute<CustomerEdit>()
        CustomerFormScreen(customerId = route.id, onBack = { navController.popBackStack() })
    }
    composable<CustomerDetail> { entry ->
        val route = entry.toRoute<CustomerDetail>()
        CustomerDetailScreen(
            customerId = route.id,
            onBack = { navController.popBackStack() },
            onEdit = { editId -> navController.navigate(CustomerEdit(editId)) { launchSingleTop = true } },
        )
    }
}
```

Rules:
- **One `<X>Nav.kt` per feature** containing routes + builder.
- **Route package == feature package** (`<base>.presentation.<feat>`). Don't rename — the
  qualified-name string is the highlight key.
- **No `ShelledScreen` wrapper here** — the shell is already rendered by `MainShell`. See
  **kmp-layout-pattern** for the per-screen `Column { BrandListToolbar ; content }` pattern.
- **`<feat>Nav` takes ONLY**: `navController: NavController` (for intra-feature sub-page nav +
  back) and any hoisted cross-feature `() -> Unit` callbacks. **Never** `onLogout`, `role`,
  `user`, `pendingSyncCount`, `onNavigateMain`, etc. — those are shell concerns.

## 4. Cross-feature navigation

A feature **cannot** import another feature's route — that would break the layering rule
(features see only `:core:domain` + `:core:ui`). Instead, hoist a `() -> Unit` callback up to
`MainShell`, which sees every route:

```kotlin
// inside features/stock — no import of features/planning's route
fun NavGraphBuilder.stockNav(
    navController: NavController,
    onOpenReorderSuggestions: () -> Unit,                    // ← hoisted callback
) {
    composable<Stock> {
        StockScreen(onOpenReorderSuggestions = onOpenReorderSuggestions)
    }
    // … internal sub-pages use navController directly
}

// inside MainShell — composeApp sees both Stock and ReorderSuggestions
stockNav(
    nestedNav,
    onOpenReorderSuggestions = { nestedNav.navigate(ReorderSuggestions) { launchSingleTop = true } },
)
```

Same for `auth → Sell` after login (`authNav(onLoggedIn = …)`).

## 5. Routes need the serialization plugin

A common foot-gun: a feature module declares `@Serializable` route objects but its
`build.gradle.kts` is missing `alias(libs.plugins.kotlin.serialization)`. **Compile passes,
runtime throws `SerializationException: Serializer for class '<Route>' is not found`**.

Apply on every feature module that declares routes:
```kotlin
plugins {
    id("<project>.kmp.compose.library")
    alias(libs.plugins.kotlin.serialization)
}
```

## 6. Logout / re-login

`appViewModel.signOut()` flips `state.isLoggedIn` → false. The outer `LaunchedEffect(isLoggedIn)`
in `AppNavHost` navigates to `Login` with `popUpTo(0) { inclusive = true }`, which tears down
`MainRoot` + its nested NavController. Re-login navigates back to `MainRoot`, which gets a fresh
`rememberNavController()` and starts at `Sell` again.

You should **not** keep a nested `NavController` reference outside `MainShell` — its lifecycle is
the `MainRoot` composition.

## 7. Anti-patterns to flag

- **Per-feature `ShelledScreen` wrapper** around the screen → kills the single-shell perf win,
  fixes drift. The shell renders in `MainShell` only.
- **Feature importing another feature's route** (`import …presentation.planning.ReorderSuggestions`
  in `:features:stock`) → fail the cross-feature audit; use a hoisted callback.
- **Routes living in a shared module** (`:features:shared`) → cycle risk, and contradicts the
  per-feature layering. Routes belong to their feature.
- **`@Serializable` route without the serialization plugin** on the feature's build file → A28-
  style runtime crash. Audit can grep for `@Serializable` declarations and check the build file
  declares the plugin.
- **`navController` threaded through composable params instead of staying inside `<feat>Nav`** →
  Screens should expose semantic callbacks (`onOpenCustomer(id)`), not a NavController.
- **Sub-page rendering its own back arrow** → use `BrandListToolbar(onBack = …)` (see
  **kmp-layout-pattern**).
- **`MainShell` reading `currentRoute` from outside the nested NavController** → derive from
  `nestedNav.currentBackStackEntryAsState()`.
- **Outer NavHost with more than `Login` + `MainRoot`** → all other destinations belong in the
  nested host so the shell stays composed.
