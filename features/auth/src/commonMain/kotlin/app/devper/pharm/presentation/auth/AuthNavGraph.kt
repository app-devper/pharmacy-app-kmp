package app.devper.pharm.presentation.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.authGraph(onLoggedIn: () -> Unit) {
    composable<Login> {
        LoginScreen(onLoggedIn = onLoggedIn)
    }
}
