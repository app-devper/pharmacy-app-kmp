package app.devper.pharm.presentation.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Login

fun NavGraphBuilder.authNav(onLoggedIn: () -> Unit) {
    composable<Login> {
        LoginScreen(onLoggedIn = onLoggedIn)
    }
}
