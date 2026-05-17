package app.devper.pharm.presentation.stockcount

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.devper.pharm.presentation.navigation.ShelledScreen

fun NavGraphBuilder.stockCountsGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
) {
    composable<StockCounts> {
        ShelledScreen(
            title = "นับสต็อก",
            currentRoute = StockCounts::class.qualifiedName!!,
            navController = navController,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            StockCountsScreen(
                onNewCount = { navController.navigate(StockCountNew) { launchSingleTop = true } },
            )
        }
    }
    composable<StockCountNew> {
        StockCountFormScreen(onBack = { navController.popBackStack() })
    }
}
