package app.devper.pharm.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import app.devper.pharm.presentation.AppViewModel
import app.devper.pharm.presentation.auth.Login
import app.devper.pharm.presentation.auth.authGraph
import app.devper.pharm.presentation.bulkimport.bulkImportGraph
import app.devper.pharm.presentation.customers.customersGraph
import app.devper.pharm.presentation.expiry.expiryGraph
import app.devper.pharm.presentation.help.helpGraph
import app.devper.pharm.presentation.imports.importsGraph
import app.devper.pharm.presentation.ky.kyGraph
import app.devper.pharm.presentation.movements.movementsGraph
import app.devper.pharm.presentation.offlinesync.offlineSyncGraph
import app.devper.pharm.presentation.planning.planningGraph
import app.devper.pharm.presentation.profile.profileGraph
import app.devper.pharm.presentation.reports.reportsGraph
import app.devper.pharm.presentation.saleshistory.salesHistoryGraph
import app.devper.pharm.presentation.sell.Sell
import app.devper.pharm.presentation.sell.sellGraph
import app.devper.pharm.presentation.settings.settingsGraph
import app.devper.pharm.presentation.stock.stockGraph
import app.devper.pharm.presentation.stockcount.stockCountsGraph
import app.devper.pharm.presentation.suppliers.suppliersGraph
import app.devper.pharm.presentation.users.usersGraph
import app.devper.pharm.ui.designsystem.TopbarUser
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost(viewModel: AppViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val isLoggedIn = state.isLoggedIn
    val pendingSyncCount = state.pendingSyncCount
    val role = state.role
    val user: TopbarUser? = if (state.userDisplayName.isNotBlank()) {
        TopbarUser(
            initial = state.userInitial.ifBlank { state.userDisplayName.take(1) },
            name = state.userDisplayName,
            role = state.role.name,
        )
    } else null
    val navController = rememberNavController()

    val start: Any = if (isLoggedIn) Sell else Login
    val onLogout = viewModel::signOut

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate(Login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = start) {
        authGraph(navController)
        sellGraph(navController, onLogout, pendingSyncCount, role, user)
        stockGraph(navController, onLogout, pendingSyncCount, role, user)
        customersGraph(navController, onLogout, pendingSyncCount, role, user)
        salesHistoryGraph(navController, onLogout, pendingSyncCount, role, user)
        settingsGraph(navController, onLogout, pendingSyncCount, role, user)
        movementsGraph(navController, onLogout, pendingSyncCount, role, user)
        suppliersGraph(navController, onLogout, pendingSyncCount, role, user)
        importsGraph(navController, onLogout, pendingSyncCount, role, user)
        bulkImportGraph(navController, onLogout, pendingSyncCount, role, user)
        stockCountsGraph(navController, onLogout, pendingSyncCount, role, user)
        expiryGraph(navController, onLogout, pendingSyncCount, role, user)
        planningGraph(navController, onLogout, pendingSyncCount, role, user)
        reportsGraph(navController, onLogout, pendingSyncCount, role, user)
        kyGraph(navController, onLogout, pendingSyncCount, role, user)
        offlineSyncGraph(navController, onLogout, pendingSyncCount, role, user)
        helpGraph(navController, onLogout, pendingSyncCount, role, user)
        profileGraph(navController, onLogout, pendingSyncCount, role, user)
        usersGraph(navController, onLogout, pendingSyncCount, role, user)
    }
}
