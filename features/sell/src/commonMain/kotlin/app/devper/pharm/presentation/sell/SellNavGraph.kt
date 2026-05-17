package app.devper.pharm.presentation.sell

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.devper.pharm.presentation.navigation.ShelledScreen

fun NavGraphBuilder.sellGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
) {
    composable<Sell> {
        ShelledScreen(
            title = "ขายยา",
            currentRoute = Sell::class.qualifiedName!!,
            navController = navController,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            SellScreen(
                onOpenCart = { navController.navigate(Cart) { launchSingleTop = true } },
            )
        }
    }
    composable<Cart> {

        CartScreen(onBack = { navController.popBackStack() })
    }
}
