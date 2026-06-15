package app.devper.pharm.ui.components

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.BottomNavItem
import app.devper.pharm.ui.designsystem.PharmBottomNav
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmSidebar
import app.devper.pharm.ui.designsystem.PharmTopbar
import app.devper.pharm.ui.designsystem.SidebarNavItem
import app.devper.pharm.ui.designsystem.TopbarUser
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val admin: Boolean = false,
)

@Immutable
data class SidebarState(
    val collapsed: Boolean = false,
    val canCollapse: Boolean = false,
    val toggle: () -> Unit = {},
)

val LocalSidebarState = staticCompositionLocalOf { SidebarState() }

private fun Role.canSeeAdminNav(): Boolean = this == Role.SUPER || this == Role.ADMIN || this == Role.MANAGER

@Composable
fun AppShell(
    title: String,
    items: List<NavItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,

    pendingSyncCount: Int = 0,

    user: TopbarUser? = null,
    role: Role = Role.UNKNOWN,
    onProfileClick: (() -> Unit)? = null,
    bottomNavItems: List<NavItem> = emptyList(),
    isSubPage: Boolean = false,
    onSubPageBack: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val size = remember(maxWidth) { WindowSize.fromWidth(maxWidth) }

        val sidebarItems = remember(items, role) {
            items
                .filter { !it.admin || role.canSeeAdminNav() }
                .map { SidebarNavItem(id = it.route, icon = it.icon, admin = it.admin, label = it.label) }
        }

        val bottomItems = remember(bottomNavItems, role) {
            bottomNavItems
                .filter { !it.admin || role.canSeeAdminNav() }
                .map { BottomNavItem(id = it.route, label = it.label, icon = it.icon) }
        }

        val subPageController = remember { SubPageBarController() }
        CompositionLocalProvider(LocalSubPageBarController provides subPageController) {
            if (size.isCompact) {
                CompactShell(
                    title = title,
                    sidebarItems = sidebarItems,
                    bottomItems = bottomItems,
                    currentRoute = currentRoute,
                    onNavigate = onNavigate,
                    onLogout = onLogout,
                    pendingSyncCount = pendingSyncCount,
                    user = user,
                    onProfileClick = onProfileClick,
                    isSubPage = isSubPage,
                    onSubPageBack = onSubPageBack,
                    content = content,
                )
            } else {
                ExpandedShell(
                    title = title,
                    sidebarItems = sidebarItems,
                    currentRoute = currentRoute,
                    onNavigate = onNavigate,
                    onLogout = onLogout,
                    pendingSyncCount = pendingSyncCount,
                    user = user,
                    onProfileClick = onProfileClick,
                    isSubPage = isSubPage,
                    onSubPageBack = onSubPageBack,
                    content = content,
                )
            }
        }
    }
}

@Composable
private fun CompactShell(
    title: String,
    sidebarItems: List<SidebarNavItem>,
    bottomItems: List<BottomNavItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    user: TopbarUser?,
    onProfileClick: (() -> Unit)?,
    isSubPage: Boolean,
    onSubPageBack: (() -> Unit)?,
    content: @Composable () -> Unit,
) {
    val t = pharmTokens
    var drawerOpen by remember { mutableStateOf(false) }
    val subPage = LocalSubPageBarController.current?.content

    Box(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(t.colors.surface),
            )
            PharmTopbar(
                title = if (isSubPage) subPage?.title ?: title else title,
                user = if (isSubPage) null else user,
                showHamburger = !isSubPage,
                showThemeToggle = false,
                showStatus = false,
                compactUserMenu = !isSubPage,
                onBack = if (isSubPage) subPage?.onBack ?: onSubPageBack else null,
                actions = if (isSubPage) subPage?.actions else null,
                onHamburger = { drawerOpen = true },
                onLogout = onLogout,
                onProfileClick = onProfileClick,
                trailing = {
                    if (pendingSyncCount > 0) PendingSyncBadge(count = pendingSyncCount)
                },
            )
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) { content() }
            if (bottomItems.isNotEmpty()) {
                PharmBottomNav(
                    items = bottomItems,
                    activeId = currentRoute,
                    onSelect = onNavigate,
                    moreLabel = pharmStrings.commonMenu,
                    moreIcon = PharmIcons.Hamburger,
                    onMore = { drawerOpen = true },
                )
            }
        }

        if (drawerOpen) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(t.colors.scrim)
                    .clickable(onClick = { drawerOpen = false }),
            )
            PharmSidebar(
                activeId = currentRoute,
                onSelect = { id ->
                    drawerOpen = false
                    onNavigate(id)
                },
                items = sidebarItems,
            )
        }
    }
}

@Composable
private fun ExpandedShell(
    title: String,
    sidebarItems: List<SidebarNavItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    user: TopbarUser?,
    onProfileClick: (() -> Unit)?,
    isSubPage: Boolean,
    onSubPageBack: (() -> Unit)?,
    content: @Composable () -> Unit,
) {
    val t = pharmTokens
    val sidebar = LocalSidebarState.current
    val subPage = LocalSubPageBarController.current?.content

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
    ) {

        PharmSidebar(
            activeId = currentRoute,
            onSelect = onNavigate,
            items = sidebarItems,
            collapsed = sidebar.collapsed,
            onToggleCollapse = if (sidebar.canCollapse) sidebar.toggle else null,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(t.colors.surface),
            )
            PharmTopbar(
                title = if (isSubPage) subPage?.title ?: title else title,
                user = if (isSubPage) null else user,
                onBack = if (isSubPage) subPage?.onBack ?: onSubPageBack else null,
                actions = if (isSubPage) subPage?.actions else null,
                onLogout = onLogout,
                onProfileClick = onProfileClick,
                trailing = {
                    if (pendingSyncCount > 0) PendingSyncBadge(count = pendingSyncCount)
                },
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
            ) { content() }
        }
    }
}

@Composable
private fun PendingSyncBadge(count: Int) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .clip(t.shapes.md)
            .background(t.colors.dangerBg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.CloudOff,
            contentDescription = null,
            tint = t.colors.dangerFg,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = pharmStrings.commonPendingSyncBadge(count),
            style = PharmText.badge.copy(color = t.colors.dangerFg),
        )
    }
}
