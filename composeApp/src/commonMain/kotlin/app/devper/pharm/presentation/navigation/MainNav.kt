package app.devper.pharm.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.devper.pharm.common.platform.UnsavedChangesHandler
import app.devper.pharm.presentation.AppViewModel
import app.devper.pharm.presentation.bulkimport.bulkImportNav
import app.devper.pharm.presentation.customers.customersNav
import app.devper.pharm.presentation.expiry.Expiry
import app.devper.pharm.presentation.expiry.expiryNav
import app.devper.pharm.presentation.help.helpNav
import app.devper.pharm.presentation.imports.ImportNew
import app.devper.pharm.presentation.imports.Imports
import app.devper.pharm.presentation.imports.importsNav
import app.devper.pharm.presentation.ky.kyNav
import app.devper.pharm.presentation.labels.labelPrintNav
import app.devper.pharm.presentation.movements.movementsNav
import app.devper.pharm.presentation.offlinesync.offlineSyncNav
import app.devper.pharm.presentation.planning.ReorderSuggestions
import app.devper.pharm.presentation.planning.planningNav
import app.devper.pharm.presentation.profile.Profile
import app.devper.pharm.presentation.profile.profileNav
import app.devper.pharm.presentation.reports.reportsNav
import app.devper.pharm.presentation.saleshistory.salesHistoryNav
import app.devper.pharm.presentation.sell.Sell
import app.devper.pharm.presentation.sell.sellNav
import app.devper.pharm.presentation.settings.Settings as SettingsRoute
import app.devper.pharm.presentation.settings.SettingsDialog
import app.devper.pharm.presentation.settings.settingsNav
import app.devper.pharm.presentation.stock.DrugEdit
import app.devper.pharm.presentation.stock.stockNav
import app.devper.pharm.presentation.stockcount.stockCountsNav
import app.devper.pharm.presentation.suppliers.suppliersNav
import app.devper.pharm.presentation.users.usersNav
import app.devper.pharm.ui.components.AppShell
import app.devper.pharm.ui.designsystem.TopbarUser
import app.devper.pharm.ui.i18n.pharmStrings
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data object MainRoot

@Composable
fun MainShell(appViewModel: AppViewModel) {
    val unsavedChangesHandler = koinInject<UnsavedChangesHandler>()
    val state by appViewModel.state.collectAsStateWithLifecycle()
    val nestedNav = rememberNavController()
    val backEntry by nestedNav.currentBackStackEntryAsState()
    val destination = destinationFor(backEntry?.destination?.route)
    val title = destination?.title?.invoke(pharmStrings).orEmpty()
    val sectionKey = destination?.sectionKey
    val isSubPage = destination?.isSubPage == true
    val navItems = rememberMainNavItems()
    var settingsOpen by remember { mutableStateOf(false) }

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
            if (key == k(SettingsRoute::class)) {
                settingsOpen = true
            } else if (key != sectionKey) {
                routeForKey(key)?.let { route ->
                    nestedNav.navigateToSection(route)
                }
            }
        },
        onLogout = appViewModel::signOut,
        pendingSyncCount = state.pendingSyncCount,
        onSyncClick = appViewModel::syncNow,
        role = state.role,
        user = user,
        onProfileClick = { nestedNav.navigate(Profile) { launchSingleTop = true } },
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
            profileNav(nestedNav)
            usersNav(nestedNav)
        }
    }

    SettingsDialog(
        open = settingsOpen,
        onDismiss = { settingsOpen = false },
    )
}

internal fun NavController.navigateToSection(route: Any) {
    popSubPagesOffTheStack()
    navigate(route) {
        launchSingleTop = true
        restoreState = route != Sell
        popUpTo(Sell) { saveState = true }
    }
}

private fun NavController.popSubPagesOffTheStack() {
    while (destinationFor(currentDestination?.route)?.isSubPage == true) {
        if (!popBackStack()) return
    }
}
