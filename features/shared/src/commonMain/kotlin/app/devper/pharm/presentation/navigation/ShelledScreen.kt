package app.devper.pharm.presentation.navigation

import androidx.compose.runtime.Composable
import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.components.AppShell
import app.devper.pharm.ui.designsystem.TopbarUser

@Composable
fun ShelledScreen(
    title: String,
    currentRoute: String,
    onNavigateMain: (Any) -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    role: Role = Role.UNKNOWN,
    user: TopbarUser? = null,
    content: @Composable () -> Unit,
) {
    val nav = LocalMainNav.current
    AppShell(
        title = title,
        items = nav.items,
        currentRoute = currentRoute,
        onNavigate = { destKey ->
            if (destKey == currentRoute) return@AppShell
            nav.routeForKey(destKey)?.let(onNavigateMain)
        },
        onLogout = onLogout,
        pendingSyncCount = pendingSyncCount,
        role = role,
        user = user,
        onProfileClick = onProfileClick,
        content = content,
    )
}
