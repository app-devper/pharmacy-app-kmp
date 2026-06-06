package app.devper.pharm.presentation.reports

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Reports

@Serializable
data object Profit

@Serializable
data object Eod

fun NavGraphBuilder.reportsNav(navController: NavController) {
    composable<Reports> {
        ReportsScreen(onCloseEod = { navController.navigate(Eod) })
    }
    composable<Profit> {
        ProfitScreen()
    }
    composable<Eod> {
        EodScreen(onBack = { navController.popBackStack() })
    }
}
