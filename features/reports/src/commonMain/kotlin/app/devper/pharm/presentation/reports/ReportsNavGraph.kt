package app.devper.pharm.presentation.reports

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.devper.pharm.presentation.navigation.ShelledScreen

fun NavGraphBuilder.reportsGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
) {
    composable<Reports> {
        ShelledScreen(
            title = "รายงานสรุป",
            currentRoute = Reports::class.qualifiedName!!,
            navController = navController,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            ReportsScreen(onCloseEod = { navController.navigate(Eod) })
        }
    }
    composable<Profit> {
        ShelledScreen(
            title = "กำไรต่อยา",
            currentRoute = Profit::class.qualifiedName!!,
            navController = navController,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            ProfitScreen()
        }
    }
    composable<Eod> {
        ShelledScreen(
            title = "ปิดยอดสิ้นวัน",
            currentRoute = Eod::class.qualifiedName!!,
            navController = navController,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            EodScreen()
        }
    }
}
