package app.devper.pharm.presentation.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Profile

fun NavGraphBuilder.profileNav() {
    composable<Profile> {
        ProfileScreen()
    }
}
