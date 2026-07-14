package app.devper.pharm.presentation.planning

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object LowStock

@Serializable
data object ReorderSuggestions

fun NavGraphBuilder.planningNav(
    navController: NavController,
    onOpenPurchaseOrder: () -> Unit,
    onOpenDrug: (drugId: String) -> Unit,
) {
    composable<LowStock> {
        LowStockScreen(onOpenDrug = onOpenDrug)
    }
    composable<ReorderSuggestions> {
        ReorderSuggestionsScreen(
            onBack = { navController.popBackStack() },
            onOpenPurchaseOrder = onOpenPurchaseOrder,
        )
    }
}
