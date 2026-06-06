package app.devper.pharm.presentation.saleshistory

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SalesHistory

fun NavGraphBuilder.salesHistoryNav() {
    composable<SalesHistory> {
        SalesHistoryScreen()
    }
}
