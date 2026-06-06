package app.devper.pharm.presentation.users

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data object Users

@Serializable
data object UserAdd

@Serializable
data class UserEdit(val id: String)

fun NavGraphBuilder.usersNav(navController: NavController) {
    composable<Users> {
        UsersListScreen(
            onAddUser = { navController.navigate(UserAdd) { launchSingleTop = true } },
            onEditUser = { user -> navController.navigate(UserEdit(user.id)) { launchSingleTop = true } },
        )
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
