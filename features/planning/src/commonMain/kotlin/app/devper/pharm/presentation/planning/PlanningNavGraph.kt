package app.devper.pharm.presentation.planning

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.devper.pharm.presentation.navigation.ShelledScreen

fun NavGraphBuilder.planningGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
    onNavigateMain: (Any) -> Unit,
    onProfileClick: () -> Unit,
) {
    composable<LowStock> {
        ShelledScreen(
            title = "ยาใกล้หมด",
            currentRoute = LowStock::class.qualifiedName!!,
            onNavigateMain = onNavigateMain,
            onProfileClick = onProfileClick,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            LowStockScreen()
        }
    }
    composable<ReorderSuggestions> {
        ReorderSuggestionsScreen(onBack = { navController.popBackStack() })
    }
}
