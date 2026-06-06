package app.devper.pharm.presentation.offlinesync

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object OfflineSync

fun NavGraphBuilder.offlineSyncNav() {
    composable<OfflineSync> {
        OfflineSyncScreen()
    }
}
