package app.devper.pharm.presentation.sell

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Sell

@Serializable
data object Cart

fun NavGraphBuilder.sellNav(navController: NavController) {
    composable<Sell> {
        SellScreen(
            onOpenCart = { navController.navigate(Cart) { launchSingleTop = true } },
        )
    }
    composable<Cart> {
        CartScreen(onBack = { navController.popBackStack() })
    }
}
