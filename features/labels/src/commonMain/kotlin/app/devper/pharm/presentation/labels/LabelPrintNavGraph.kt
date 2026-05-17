package app.devper.pharm.presentation.labels

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.devper.pharm.domain.model.Role
import app.devper.pharm.presentation.navigation.ShelledScreen
import app.devper.pharm.ui.designsystem.TopbarUser

fun NavGraphBuilder.labelPrintGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
) {
    composable<LabelPrint> {
        ShelledScreen(
            title = "พิมพ์ฉลาก",
            currentRoute = LabelPrint::class.qualifiedName!!,
            navController = navController,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            LabelPrintScreen()
        }
    }
}
