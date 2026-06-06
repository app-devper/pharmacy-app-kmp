package app.devper.pharm.presentation.expiry

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Expiry

fun NavGraphBuilder.expiryNav() {
    composable<Expiry> {
        ExpiryScreen()
    }
}
