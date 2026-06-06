package app.devper.pharm.presentation.help

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Help

fun NavGraphBuilder.helpNav() {
    composable<Help> {
        HelpScreen()
    }
}
