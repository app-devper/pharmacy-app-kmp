package app.devper.pharm.presentation.movements

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.devper.pharm.presentation.navigation.ShelledScreen

fun NavGraphBuilder.movementsGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
) {
    composable<Movements> {
        ShelledScreen(
            title = "ประวัติเคลื่อนไหวสต็อก",
            currentRoute = Movements::class.qualifiedName!!,
            navController = navController,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            MovementsScreen()
        }
    }
}
