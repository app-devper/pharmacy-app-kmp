package app.devper.pharm.presentation.stockcount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object StockCounts

@Serializable
data object StockCountNew

fun NavGraphBuilder.stockCountsNav(navController: NavController) {
    composable<StockCounts> {
        StockCountsScreen(
            onNewCount = { navController.navigate(StockCountNew) { launchSingleTop = true } },
        )
    }
    composable<StockCountNew> {
        StockCountFormScreen(onBack = { navController.popBackStack() })
    }
}
