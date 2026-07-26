package app.devper.pharm.presentation.navigation

import app.devper.pharm.ui.i18n.navTitle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.common.platform.UnsavedChangesHandler
import app.devper.pharm.presentation.AppViewModel
import app.devper.pharm.presentation.bulkimport.BulkImport
import app.devper.pharm.presentation.bulkimport.bulkImportNav
import app.devper.pharm.presentation.customers.CustomerAdd
import app.devper.pharm.presentation.customers.CustomerDetail
import app.devper.pharm.presentation.customers.CustomerEdit
import app.devper.pharm.presentation.customers.Customers
import app.devper.pharm.presentation.customers.customersNav
import app.devper.pharm.presentation.expiry.Expiry
import app.devper.pharm.presentation.expiry.expiryNav
import app.devper.pharm.presentation.help.Help
import app.devper.pharm.presentation.help.helpNav
import app.devper.pharm.presentation.imports.ImportDetail
import app.devper.pharm.presentation.imports.ImportEdit
import app.devper.pharm.presentation.imports.ImportNew
import app.devper.pharm.presentation.imports.Imports
import app.devper.pharm.presentation.imports.importsNav
import app.devper.pharm.presentation.ky.Ky9
import app.devper.pharm.presentation.ky.Ky9Add
import app.devper.pharm.presentation.ky.Ky10
import app.devper.pharm.presentation.ky.Ky10Add
import app.devper.pharm.presentation.ky.Ky11
import app.devper.pharm.presentation.ky.Ky11Add
import app.devper.pharm.presentation.ky.Ky12
import app.devper.pharm.presentation.ky.Ky12Add
import app.devper.pharm.presentation.ky.kyNav
import app.devper.pharm.presentation.labels.LabelPrint
import app.devper.pharm.presentation.labels.labelPrintNav
import app.devper.pharm.presentation.movements.Movements
import app.devper.pharm.presentation.movements.movementsNav
import app.devper.pharm.presentation.offlinesync.OfflineSync
import app.devper.pharm.presentation.offlinesync.offlineSyncNav
import app.devper.pharm.presentation.planning.LowStock
import app.devper.pharm.presentation.planning.ReorderSuggestions
import app.devper.pharm.presentation.planning.planningNav
import app.devper.pharm.presentation.profile.Profile
import app.devper.pharm.presentation.profile.profileNav
import app.devper.pharm.presentation.reports.Eod
import app.devper.pharm.presentation.reports.Profit
import app.devper.pharm.presentation.reports.Reports
import app.devper.pharm.presentation.reports.reportsNav
import app.devper.pharm.presentation.saleshistory.SalesHistory
import app.devper.pharm.presentation.saleshistory.salesHistoryNav
import app.devper.pharm.presentation.sell.Cart
import app.devper.pharm.presentation.sell.Sell
import app.devper.pharm.presentation.sell.sellNav
import app.devper.pharm.presentation.settings.Settings as SettingsRoute
import app.devper.pharm.presentation.settings.settingsNav
import app.devper.pharm.presentation.stock.DrugAdd
import app.devper.pharm.presentation.stock.DrugAdjust
import app.devper.pharm.presentation.stock.DrugEdit
import app.devper.pharm.presentation.stock.DrugHistory
import app.devper.pharm.presentation.stock.DrugLots
import app.devper.pharm.presentation.stock.Stock
import app.devper.pharm.presentation.stock.stockNav
import app.devper.pharm.presentation.stockcount.StockCountNew
import app.devper.pharm.presentation.stockcount.StockCounts
import app.devper.pharm.presentation.stockcount.stockCountsNav
import app.devper.pharm.presentation.suppliers.SupplierAdd
import app.devper.pharm.presentation.suppliers.SupplierEdit
import app.devper.pharm.presentation.suppliers.Suppliers
import app.devper.pharm.presentation.suppliers.suppliersNav
import app.devper.pharm.presentation.users.UserAdd
import app.devper.pharm.presentation.users.UserEdit
import app.devper.pharm.presentation.users.Users
import app.devper.pharm.presentation.users.usersNav
import app.devper.pharm.ui.components.AppShell
import app.devper.pharm.ui.components.NavItem
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.TopbarUser
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.pharmStrings
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import kotlin.reflect.KClass

@Serializable
data object MainRoot

@Composable
fun MainShell(appViewModel: AppViewModel) {
    val unsavedChangesHandler = koinInject<UnsavedChangesHandler>()
    val state by appViewModel.state.collectAsStateWithLifecycle()
    val nestedNav = rememberNavController()
    val backEntry by nestedNav.currentBackStackEntryAsState()
    val (title, sectionKey) = destInfoFor(backEntry?.destination?.route)
    val currentRouteBase = backEntry?.destination?.route?.substringBefore('/')?.substringBefore('?')
    val isSubPage = currentRouteBase in SUB_PAGE_ROUTE_KEYS
    val navItems = rememberMainNavItems()
    val bottomNavItems = rememberBottomNavItems()

    val user: TopbarUser? = if (state.userDisplayName.isNotBlank()) {
        TopbarUser(
            initial = state.userInitial.ifBlank { state.userDisplayName.take(1) },
            name = state.userDisplayName,
            role = state.role.name,
        )
    } else {
        null
    }

    AppShell(
        title = title,
        items = navItems,
        currentRoute = sectionKey ?: "",
        onNavigate = { key ->
            if (key != sectionKey) {
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
        onSyncClick = appViewModel::syncNow,
        role = state.role,
        user = user,
        onProfileClick = { nestedNav.navigate(Profile) { launchSingleTop = true } },
        bottomNavItems = bottomNavItems,
        isSubPage = isSubPage,
        onSubPageBack = { nestedNav.popBackStack() },
        onUnsavedChangesChanged = unsavedChangesHandler::setHasUnsavedChanges,
    ) {
        NavHost(navController = nestedNav, startDestination = Sell) {
            sellNav(nestedNav)
            stockNav(
                nestedNav,
                onOpenReorderSuggestions = { nestedNav.navigate(ReorderSuggestions) { launchSingleTop = true } },
                onOpenExpiry = { nestedNav.navigate(Expiry) { launchSingleTop = true } },
                onOpenImports = { nestedNav.navigate(Imports) { launchSingleTop = true } },
            )
            customersNav(nestedNav)
            salesHistoryNav()
            settingsNav()
            movementsNav()
            suppliersNav(nestedNav)
            importsNav(nestedNav)
            bulkImportNav()
            stockCountsNav(nestedNav)
            expiryNav()
            labelPrintNav()
            planningNav(
                nestedNav,
                onOpenPurchaseOrder = { nestedNav.navigate(ImportNew) { launchSingleTop = true } },
                onOpenDrug = { drugId -> nestedNav.navigate(DrugEdit(drugId)) { launchSingleTop = true } },
            )
            reportsNav(nestedNav)
            kyNav(nestedNav)
            offlineSyncNav()
            helpNav()
            profileNav()
            usersNav(nestedNav)
        }
    }
}
