package app.devper.pharm.presentation.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Settings

fun NavGraphBuilder.settingsNav() {
    composable<Settings> {
        SettingsScreen()
    }
}
