package app.devper.pharm.presentation.stock

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data object Stock

@Serializable
data object DrugAdd

@Serializable
data class DrugEdit(val id: String)

@Serializable
data class DrugHistory(val drugId: String, val drugName: String)

@Serializable
data class DrugLots(val drugId: String, val drugName: String)

@Serializable
data class DrugAdjust(val drugId: String, val drugName: String)

fun NavGraphBuilder.stockNav(
    navController: NavController,
    onOpenReorderSuggestions: () -> Unit,
    onOpenExpiry: () -> Unit,
) {
    composable<Stock> {
        StockScreen(
            onAddDrug = { navController.navigate(DrugAdd) { launchSingleTop = true } },
            onEditDrug = { id -> navController.navigate(DrugEdit(id)) { launchSingleTop = true } },
            onOpenLots = { id, name -> navController.navigate(DrugLots(id, name)) { launchSingleTop = true } },
            onOpenAdjust = { id, name -> navController.navigate(DrugAdjust(id, name)) { launchSingleTop = true } },
            onOpenHistory = { id, name -> navController.navigate(DrugHistory(id, name)) { launchSingleTop = true } },
            onOpenReorderSuggestions = onOpenReorderSuggestions,
            onOpenExpiry = onOpenExpiry,
        )
    }
    composable<DrugAdd> {
        DrugFormScreen(
            drugId = null,
            onBack = { navController.popBackStack() },
        )
    }
    composable<DrugEdit> { entry ->
        val route = entry.toRoute<DrugEdit>()
        DrugFormScreen(
            drugId = route.id,
            onBack = { navController.popBackStack() },
        )
    }
    composable<DrugHistory> { entry ->
        val route = entry.toRoute<DrugHistory>()
        DrugHistoryScreen(
            drugName = route.drugName,
            onBack = { navController.popBackStack() },
        )
    }
    composable<DrugLots> { entry ->
        val route = entry.toRoute<DrugLots>()
        DrugLotsScreen(
            drugId = route.drugId,
            drugName = route.drugName,
            onBack = { navController.popBackStack() },
        )
    }
    composable<DrugAdjust> { entry ->
        val route = entry.toRoute<DrugAdjust>()
        DrugAdjustScreen(
            drugId = route.drugId,
            drugName = route.drugName,
            onBack = { navController.popBackStack() },
        )
    }
}
