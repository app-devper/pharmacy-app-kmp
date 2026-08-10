---
name: kmp-navigation
description: The two-level NavHost + single-shell navigation of the pharmacy app — AppNavHost hosts authNav + MainShell; MainShell renders AppShell once around a nested NavHost composing every <feat>Nav; routes live per-feature; cross-feature jumps go through hoisted callbacks. Use when adding a screen, wiring a route, or debugging the sidebar highlight.
---

# kmp-navigation

Two-level NavHost with a single shell. The sidebar/topbar is composed **once**
at the top of `MainShell`; every feature lives inside a nested NavHost below
it. There is no per-feature `ShelledScreen` wrapper.

```
AppNavHost (outer NavHost, startDestination = Login)
  ├── authNav(onLoggedIn = …)            ← contributes composable<Login>
  └── composable<MainRoot> { MainShell(appViewModel) }
            └── AppShell(sidebar, topbar) rendered ONCE
                  └── NavHost(nestedNav, startDestination = Sell)
                        ├── sellNav(nestedNav)
                        ├── stockNav(nestedNav, onOpenReorderSuggestions, onOpenExpiry, onOpenImports)
                        ├── customersNav(nestedNav)
                        ├── … 20 feature graphs
                        └── profileNav(nestedNav)
```

Everything lives in
`composeApp/src/commonMain/kotlin/app/devper/pharm/presentation/navigation/` —
`AppNavHost.kt`, `MainNav.kt`, `MainNavTable.kt`.

Why: the shell renders once (switching sections doesn't rebuild the sidebar,
topbar or their state), it can't drift between features, `MainShell` sees every
route so cross-feature jumps work without features importing each other, and
the auth gate is one `LaunchedEffect`, not one per feature.

## 1. `AppNavHost.kt`

```kotlin
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

    NavHost(navController = rootNav, startDestination = Login) {
        authNav(onLoggedIn = { rootNav.navigate(MainRoot) { popUpTo(Login) { inclusive = true } } })
        composable<MainRoot> { MainShell(appViewModel = viewModel) }
    }
}
```

Exactly two destinations. Anything else belongs in the nested host, or the
shell stops being composed.

## 2. `MainShell` (MainNav.kt)

```kotlin
val nestedNav = rememberNavController()
val backEntry by nestedNav.currentBackStackEntryAsState()
val (title, sectionKey) = destInfoFor(backEntry?.destination?.route)
val currentRouteBase = backEntry?.destination?.route?.substringBefore('/')?.substringBefore('?')
val isSubPage = currentRouteBase in SUB_PAGE_ROUTE_KEYS

AppShell(
    title = title,
    items = rememberMainNavItems(),
    currentRoute = sectionKey ?: "",
    onNavigate = { key -> … routeForKey(key) … },
    onLogout = appViewModel::signOut,
    isSubPage = isSubPage,
    onSubPageBack = { nestedNav.popBackStack() },
    onUnsavedChangesChanged = unsavedChangesHandler::setHasUnsavedChanges,
    …
) {
    NavHost(navController = nestedNav, startDestination = Sell) { … every <feat>Nav … }
}
```

Section navigation uses `launchSingleTop = true; restoreState = true;
popUpTo(Sell) { saveState = true }` so each section keeps its own back stack.

Settings is the one sidebar entry that is **not** a navigation: its key flips
`settingsOpen` and opens `SettingsDialog`.

## 3. `MainNavTable.kt` — the route → chrome mapping

```kotlin
private data class DestInfo(val title: (PharmStrings) -> String, val sectionKey: String?)

private val DEST_INFO: Map<String, DestInfo> = buildMap {
    fun add(route: KClass<*>, title: (PharmStrings) -> String, section: KClass<*>?) {
        put(k(route), DestInfo(title, section?.let(::k)))
    }
    add(Stock::class,     { it.navStock }, Stock::class)
    add(DrugAdd::class,   { it.navStock }, Stock::class)     // sub-page → parent section
    add(DrugHistory::class, { it.navStock }, Stock::class)
    add(BulkImport::class, { it.navBulkImport }, null)       // orphan → no sidebar highlight
    …
}
```

- The key is `route::class.qualifiedName` (`k(...)`), so **renaming a route's
  package silently breaks the sidebar highlight**.
- Titles are `(PharmStrings) -> String` lambdas, not strings — the topbar
  re-titles live when the locale changes.
- `sectionKey` decides which sidebar item is highlighted; sub-pages point at
  their parent, orphans pass `null`.
- **One entry per destination**, not per section.
- `SUB_PAGE_ROUTE_KEYS` is a separate set listing every sub-page route — it
  drives the compact topbar's back button.
- `MAIN_NAV_TABLE` holds the sidebar rows; `routeForKey(key)` resolves a
  sidebar key back to its route object.

Adding a destination means touching **three** places in this file: the route
itself in its feature, a `DEST_INFO` entry, and `SUB_PAGE_ROUTE_KEYS` if it is
a sub-page.

## 4. Per-feature `<X>Nav.kt`

File lives at `presentation/<feat>/navigation/<Feat>Nav.kt` but the **package
is `app.devper.pharm.presentation.<feat>`** — no `.navigation` segment. That is
deliberate: `DEST_INFO` keys on the qualified name.

```kotlin
package app.devper.pharm.presentation.customers

@Serializable data object Customers
@Serializable data object CustomerAdd
@Serializable data class CustomerEdit(val id: String)
@Serializable data class CustomerDetail(val id: String)

fun NavGraphBuilder.customersNav(navController: NavController) {
    composable<Customers> {
        CustomersScreen(
            onAddCustomer = { navController.navigate(CustomerAdd) { launchSingleTop = true } },
            onOpenCustomer = { id -> navController.navigate(CustomerDetail(id)) { launchSingleTop = true } },
        )
    }
    composable<CustomerAdd> { CustomerFormScreen(customerId = null, onBack = { navController.popBackStack() }) }
    composable<CustomerEdit> { entry ->
        CustomerFormScreen(customerId = entry.toRoute<CustomerEdit>().id, onBack = { navController.popBackStack() })
    }
}
```

Signature rules — `<feat>Nav` takes only what it needs:

| Shape | Features |
|---|---|
| `()` — leaf, no sub-pages | expiry, help, labels, movements, offlinesync, saleshistory, settings, bulkimport |
| `(navController)` — has sub-pages | customers, imports, ky, profile, reports, sell, stockcount, suppliers, users |
| `(navController, + hoisted callbacks)` | stock, planning |
| `(onLoggedIn)` | auth |

Never pass `onLogout`, `role`, `user` or `pendingSyncCount` into a feature graph
— those are shell concerns. Screens expose semantic callbacks
(`onOpenCustomer(id)`), never a `NavController`.

## 5. Cross-feature navigation

A feature cannot import another feature's route — features see only
`:core:domain` + `:core:ui`. Hoist a `() -> Unit` to `MainShell`, which sees
everything:

```kotlin
// :features:stock declares the parameter, not the destination
fun NavGraphBuilder.stockNav(
    navController: NavController,
    onOpenReorderSuggestions: () -> Unit,
    onOpenExpiry: () -> Unit,
    onOpenImports: () -> Unit,
) { … }

// :composeApp supplies it
stockNav(
    nestedNav,
    onOpenReorderSuggestions = { nestedNav.navigate(ReorderSuggestions) { launchSingleTop = true } },
    onOpenExpiry = { nestedNav.navigate(Expiry) { launchSingleTop = true } },
    onOpenImports = { nestedNav.navigate(Imports) { launchSingleTop = true } },
)
```

## 6. Routes need the serialization plugin

A feature module declaring `@Serializable` routes must apply
`alias(libs.plugins.kotlin.serialization)`. Without it the code **compiles** and
throws `SerializationException: Serializer for class '<Route>' is not found` at
runtime. This is the single most common wiring bug in this repo.

## 7. Logout / re-login

`appViewModel.signOut()` flips `isLoggedIn`; the outer `LaunchedEffect`
navigates to `Login` with `popUpTo(0) { inclusive = true }`, tearing down
`MainRoot` and its nested NavController. Re-login gets a fresh
`rememberNavController()` starting at `Sell`. Never hold a reference to the
nested NavController outside `MainShell` — its lifecycle is the `MainRoot`
composition.

## 8. Anti-patterns

- A per-feature shell wrapper around a screen — the shell renders in
  `MainShell` only.
- A feature importing another feature's route — use a hoisted callback.
- Routes in a shared module — they belong to their feature.
- `@Serializable` route without the serialization plugin on that module.
- A `NavController` threaded through Screen parameters.
- A sub-page drawing its own back arrow instead of `PharmListToolbar(onBack)`.
- A new destination missing from `DEST_INFO` — the topbar goes blank and the
  sidebar highlight drops.
- A sub-page missing from `SUB_PAGE_ROUTE_KEYS` — no back button in the compact
  topbar.
- More than `Login` + `MainRoot` in the outer NavHost.
