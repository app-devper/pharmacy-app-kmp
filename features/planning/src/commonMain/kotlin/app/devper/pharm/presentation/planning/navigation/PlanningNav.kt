package app.devper.pharm.presentation.planning

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object LowStock

@Serializable
data object ReorderSuggestions

fun NavGraphBuilder.planningNav(navController: NavController) {
    composable<LowStock> {
        LowStockScreen()
    }
    composable<ReorderSuggestions> {
        ReorderSuggestionsScreen(onBack = { navController.popBackStack() })
    }
}
