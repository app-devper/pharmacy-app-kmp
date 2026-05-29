package app.devper.pharm.presentation.expiry

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.TopbarUser
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.devper.pharm.presentation.navigation.ShelledScreen

fun NavGraphBuilder.expiryGraph(
    navController: NavController,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
    onNavigateMain: (Any) -> Unit,
    onProfileClick: () -> Unit,
) {
    composable<Expiry> {
        ShelledScreen(
            title = "ล็อตใกล้หมดอายุ",
            currentRoute = Expiry::class.qualifiedName!!,
            onNavigateMain = onNavigateMain,
            onProfileClick = onProfileClick,
            onLogout = onLogout,
            pendingSyncCount = pendingSyncCount,
            role = role,
            user = user,
        ) {
            ExpiryScreen()
        }
    }
}
