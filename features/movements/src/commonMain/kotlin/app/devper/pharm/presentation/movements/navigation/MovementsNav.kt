package app.devper.pharm.presentation.movements

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Movements

fun NavGraphBuilder.movementsNav() {
    composable<Movements> {
        MovementsScreen()
    }
}
