package app.devper.pharm.presentation.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Profile

fun NavGraphBuilder.profileNav(navController: NavController) {
    composable<Profile> {
        ProfileScreen(onBack = { navController.popBackStack() })
    }
}
