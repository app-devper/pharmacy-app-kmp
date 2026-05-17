package app.devper.pharm.presentation.settings

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.devper.pharm.presentation.bulkimport.BulkImport
import app.devper.pharm.presentation.expiry.Expiry
import app.devper.pharm.presentation.help.Help
import app.devper.pharm.presentation.imports.Imports
import app.devper.pharm.presentation.ky.Ky10
import app.devper.pharm.presentation.ky.Ky11
import app.devper.pharm.presentation.ky.Ky12
import app.devper.pharm.presentation.ky.Ky9
import app.devper.pharm.presentation.movements.Movements
import app.devper.pharm.presentation.navigation.ShelledScreen
import app.devper.pharm.presentation.offlinesync.OfflineSync
import app.devper.pharm.presentation.planning.LowStock
import app.devper.pharm.presentation.planning.ReorderSuggestions
import app.devper.pharm.presentation.profile.Profile
import app.devper.pharm.presentation.reports.Eod
import app.devper.pharm.presentation.reports.Profit
import app.devper.pharm.presentation.reports.Reports
import app.devper.pharm.presentation.stockcount.StockCounts
import app.devper.pharm.presentation.suppliers.Suppliers
import app.devper.pharm.presentation.users.Users

fun NavGraphBuilder.settingsGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
) {
    composable<Settings> {
        ShelledScreen(
            title = "ตั้งค่าระบบ",
            currentRoute = Settings::class.qualifiedName!!,
            navController = navController,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            SettingsScreen(
                onNavigate = { key -> navController.navigate(key.toRoute()) { launchSingleTop = true } },
            )
        }
    }
}

private fun SettingsMenuKey.toRoute(): Any = when (this) {
    SettingsMenuKey.Profile     -> Profile
    SettingsMenuKey.Users       -> Users
    SettingsMenuKey.Movements   -> Movements
    SettingsMenuKey.Imports     -> Imports
    SettingsMenuKey.Suppliers   -> Suppliers
    SettingsMenuKey.BulkImport  -> BulkImport
    SettingsMenuKey.StockCounts -> StockCounts
    SettingsMenuKey.Expiry      -> Expiry
    SettingsMenuKey.LowStock    -> LowStock
    SettingsMenuKey.Reorder     -> ReorderSuggestions
    SettingsMenuKey.Reports     -> Reports
    SettingsMenuKey.Profit      -> Profit
    SettingsMenuKey.Eod         -> Eod
    SettingsMenuKey.Ky9         -> Ky9
    SettingsMenuKey.Ky10        -> Ky10
    SettingsMenuKey.Ky11        -> Ky11
    SettingsMenuKey.Ky12        -> Ky12
    SettingsMenuKey.OfflineSync -> OfflineSync
    SettingsMenuKey.Help        -> Help
}
