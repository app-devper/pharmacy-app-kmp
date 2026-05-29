package app.devper.pharm.presentation.users

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import app.devper.pharm.presentation.navigation.ShelledScreen

fun NavGraphBuilder.usersGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
    onNavigateMain: (Any) -> Unit,
    onProfileClick: () -> Unit,
) {
    composable<Users> {
        ShelledScreen(
            title = "จัดการผู้ใช้งาน",
            currentRoute = Users::class.qualifiedName!!,
            onNavigateMain = onNavigateMain,
            onProfileClick = onProfileClick,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            UsersListScreen(
                onAddUser = { navController.navigate(UserAdd) { launchSingleTop = true } },
                onEditUser = { user -> navController.navigate(UserEdit(user.id)) { launchSingleTop = true } },
            )
        }
    }
    composable<UserAdd> {
        UserFormScreen(
            userId = null,
            onBack = { navController.popBackStack() },
        )
    }
    composable<UserEdit> { entry ->
        val route = entry.toRoute<UserEdit>()
        UserFormScreen(
            userId = route.id,
            onBack = { navController.popBackStack() },
        )
    }
}
