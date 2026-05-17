package app.devper.pharm.presentation.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.devper.pharm.presentation.sell.Sell

fun NavGraphBuilder.authGraph(navController: NavController) {
    composable<Login> {
        LoginScreen(
            onLoggedIn = {
                navController.navigate(Sell) {
                    popUpTo<Login> { inclusive = true }
                }
            },
        )
    }
}
