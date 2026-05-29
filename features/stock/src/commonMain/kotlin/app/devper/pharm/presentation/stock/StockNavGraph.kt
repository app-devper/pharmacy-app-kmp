package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import app.devper.pharm.presentation.movements.Movements
import app.devper.pharm.presentation.navigation.ShelledScreen
import app.devper.pharm.presentation.planning.ReorderSuggestions

fun NavGraphBuilder.stockGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
    onNavigateMain: (Any) -> Unit,
    onProfileClick: () -> Unit,
) {
    composable<Stock> {
        ShelledScreen(
            title = "สต็อกยา",
            currentRoute = Stock::class.qualifiedName!!,
            onNavigateMain = onNavigateMain,
            onProfileClick = onProfileClick,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            StockScreen(
                onAddDrug = { navController.navigate(DrugAdd) { launchSingleTop = true } },
                onEditDrug = { id -> navController.navigate(DrugEdit(id)) { launchSingleTop = true } },
                onOpenLots = { id -> navController.navigate(DrugEdit(id)) { launchSingleTop = true } },
                onOpenAdjust = { id -> navController.navigate(DrugEdit(id)) { launchSingleTop = true } },
                onOpenHistory = { navController.navigate(Movements) { launchSingleTop = true } },
                onOpenReorderSuggestions = { navController.navigate(ReorderSuggestions) { launchSingleTop = true } },
            )
        }
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
}
