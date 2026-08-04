package app.devper.pharm.ui.components

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.LocalReducedMotion
import app.devper.pharm.ui.designsystem.LocalCompactTopbarActions
import app.devper.pharm.ui.designsystem.PharmMotion
import app.devper.pharm.ui.designsystem.PharmSidebar
import app.devper.pharm.ui.designsystem.PharmTopbar
import app.devper.pharm.ui.designsystem.SidebarAccount
import app.devper.pharm.ui.designsystem.SidebarNavItem
import app.devper.pharm.ui.designsystem.TopbarUser
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable
import kotlinx.coroutines.delay

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val admin: Boolean = false,
    val pinned: Boolean = false,
    val sectionLabel: String = "",
)

@Immutable
data class SidebarState(
    val collapsed: Boolean = false,
    val canCollapse: Boolean = false,
    val toggle: () -> Unit = {},
)

val LocalSidebarState = staticCompositionLocalOf { SidebarState() }
val LocalPageTitle = staticCompositionLocalOf { "" }

private fun Role.canSeeAdminNav(): Boolean = this == Role.SUPER || this == Role.ADMIN || this == Role.MANAGER

@Composable
fun AppShell(
    title: String,
    items: List<NavItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,

    pendingSyncCount: Int = 0,
    onSyncClick: () -> Unit = {},

    user: TopbarUser? = null,
    role: Role = Role.UNKNOWN,
    onProfileClick: (() -> Unit)? = null,
    isSubPage: Boolean = false,
    onSubPageBack: (() -> Unit)? = null,
    onUnsavedChangesChanged: (Boolean) -> Unit = {},
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val size = remember(maxWidth) { WindowSize.fromWidth(maxWidth) }
        val useCompactShell = usesCompactAppShell(size)

        val sidebarItems = remember(items, role) {
            items
                .filter { !it.admin || role.canSeeAdminNav() }
                .map {
                    SidebarNavItem(
                        id = it.route,
                        icon = it.icon,
                        admin = it.admin,
                        pinned = it.pinned,
                        label = it.label,
                        sectionLabel = it.sectionLabel,
                    )
                }
        }

        val compactPageActionsController = remember { CompactPageActionsController() }
        val compactPageHeaderController = remember { CompactPageHeaderController() }
        val unsavedChangesController = remember { UnsavedChangesController() }
        LaunchedEffect(unsavedChangesController.hasUnsavedChanges) {
            onUnsavedChangesChanged(unsavedChangesController.hasUnsavedChanges)
        }
        DisposableEffect(Unit) {
            onDispose { onUnsavedChangesChanged(false) }
        }
        CompositionLocalProvider(
            LocalWindowSize provides size,
            LocalPageTitle provides title,
            LocalCompactPageActionsController provides compactPageActionsController,
            LocalCompactPageHeaderController provides compactPageHeaderController,
            LocalUnsavedChangesController provides unsavedChangesController,
        ) {
            GuardedSystemBack(isSubPage, onSubPageBack)
            if (useCompactShell) {
                CompactShell(
                    title = title,
                    drawerWidth = compactDrawerWidth(
                        windowWidth = maxWidth,
                        maxDrawerWidth = pharmTokens.dimens.sidebarWidth,
                    ),
                    sidebarItems = sidebarItems,
                    currentRoute = currentRoute,
                    onNavigate = onNavigate,
                    onLogout = onLogout,
                    pendingSyncCount = pendingSyncCount,
                    onSyncClick = onSyncClick,
                    user = user,
                    onProfileClick = onProfileClick,
                    content = content,
                )
            } else {
                ExpandedShell(
                    sidebarItems = sidebarItems,
                    currentRoute = currentRoute,
                    onNavigate = onNavigate,
                    onLogout = onLogout,
                    pendingSyncCount = pendingSyncCount,
                    onSyncClick = onSyncClick,
                    user = user,
                    onProfileClick = onProfileClick,
                    content = content,
                )
            }
            UnsavedChangesDialog(unsavedChangesController)
        }
    }
}

@Suppress("DEPRECATION")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun GuardedSystemBack(isSubPage: Boolean, onSubPageBack: (() -> Unit)?) {
    val controller = LocalUnsavedChangesController.current ?: return
    BackHandler(enabled = isSubPage && controller.hasUnsavedChanges && onSubPageBack != null) {
        controller.request { onSubPageBack?.invoke() }
    }
}

@Composable
@Suppress("DEPRECATION")
@OptIn(ExperimentalComposeUiApi::class)
private fun CompactShell(
    title: String,
    drawerWidth: Dp,
    sidebarItems: List<SidebarNavItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    onSyncClick: () -> Unit,
    user: TopbarUser?,
    onProfileClick: (() -> Unit)?,
    content: @Composable () -> Unit,
) {
    val t = pharmTokens
    val reducedMotion = LocalReducedMotion.current
    var drawerOpen by remember { mutableStateOf(false) }
    var drawerMounted by remember { mutableStateOf(false) }
    val pageActions = LocalCompactPageActionsController.current?.content
    val pageHeader = LocalCompactPageHeaderController.current?.content
    val unsavedChanges = LocalUnsavedChangesController.current
    val guardedNavigate: (String) -> Unit = { id ->
        unsavedChanges?.request { onNavigate(id) } ?: onNavigate(id)
    }
    val guardedLogout = { unsavedChanges?.request(onLogout) ?: onLogout() }
    val guardedProfileClick = onProfileClick?.let { action ->
        { unsavedChanges?.request(action) ?: action() }
    }
    val account = user?.let { SidebarAccount(initial = it.initial, name = it.name, role = it.role) }
    val settingsRoute = sidebarItems.firstOrNull { it.label == pharmStrings.navSettings }?.id
    val helpRoute = sidebarItems.firstOrNull { it.label == pharmStrings.navHelp }?.id
    LaunchedEffect(drawerOpen, drawerMounted, reducedMotion) {
        if (drawerMounted && !drawerOpen) {
            delay(if (reducedMotion) 0L else PharmMotion.Medium.toLong())
            drawerMounted = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(t.colors.bgPage),
            )
            PharmTopbar(
                title = pageHeader?.title ?: title,
                showHamburger = pageHeader == null,
                showThemeToggle = false,
                showStatus = false,
                compactUserMenu = true,
                showDivider = false,
                backgroundColor = t.colors.bgPage,
                onBack = pageHeader?.onBack,
                actions = (if (pageHeader != null) pageHeader.actions else pageActions?.actions)?.let { actions ->
                    {
                        CompositionLocalProvider(LocalCompactTopbarActions provides true) {
                            actions()
                        }
                    }
                },
                onHamburger = {
                    drawerMounted = true
                    drawerOpen = true
                },
                trailing = {
                    if (pendingSyncCount > 0) {
                        PendingSyncBadge(
                            count = pendingSyncCount,
                            onClick = onSyncClick,
                            iconOnly = true,
                        )
                    }
                },
            )
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) { content() }
        }

        if (drawerMounted) {
            Dialog(
                onDismissRequest = { drawerOpen = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                ),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val closeMenuDescription = pharmStrings.commonCloseMenu
                    AnimatedVisibility(
                        visible = drawerOpen,
                        enter = if (reducedMotion) EnterTransition.None else fadeIn(tween(PharmMotion.Fast)),
                        exit = if (reducedMotion) ExitTransition.None else fadeOut(tween(PharmMotion.Fast)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(t.colors.scrim)
                                .pharmClickable(shape = RectangleShape, onClick = { drawerOpen = false })
                                .semantics { contentDescription = closeMenuDescription },
                        )
                    }
                    AnimatedVisibility(
                        visible = drawerOpen,
                        enter = if (reducedMotion) {
                            EnterTransition.None
                        } else {
                            slideInHorizontally(tween(PharmMotion.Medium)) { -it } + fadeIn(tween(PharmMotion.Fast))
                        },
                        exit = if (reducedMotion) {
                            ExitTransition.None
                        } else {
                            slideOutHorizontally(tween(PharmMotion.Medium)) { -it } + fadeOut(tween(PharmMotion.Fast))
                        },
                    ) {
                        PharmSidebar(
                            activeId = currentRoute,
                            onSelect = { id ->
                                drawerOpen = false
                                guardedNavigate(id)
                            },
                            items = sidebarItems,
                            expandedWidth = drawerWidth,
                            applySystemInsets = true,
                            onToggleCollapse = { drawerOpen = false },
                            account = account,
                            onProfileClick = guardedProfileClick?.let { action ->
                                {
                                    drawerOpen = false
                                    action()
                                }
                            },
                            onSettingsClick = settingsRoute?.let { route ->
                                {
                                    drawerOpen = false
                                    guardedNavigate(route)
                                }
                            },
                            onHelpClick = helpRoute?.let { route ->
                                {
                                    drawerOpen = false
                                    guardedNavigate(route)
                                }
                            },
                            onLogout = {
                                drawerOpen = false
                                guardedLogout()
                            },
                        )
                    }
                }
            }
        }
    }
}

internal fun usesCompactAppShell(windowSize: WindowSize): Boolean = windowSize.isCompactShell

internal fun compactDrawerWidth(windowWidth: Dp, maxDrawerWidth: Dp): Dp =
    if (windowWidth < PharmBreakpoint.Medium) windowWidth else minOf(windowWidth, maxDrawerWidth)

@Composable
private fun ExpandedShell(
    sidebarItems: List<SidebarNavItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    pendingSyncCount: Int,
    onSyncClick: () -> Unit,
    user: TopbarUser?,
    onProfileClick: (() -> Unit)?,
    content: @Composable () -> Unit,
) {
    val t = pharmTokens
    val sidebar = LocalSidebarState.current
    val sidebarCollapsed = sidebar.collapsed
    val unsavedChanges = LocalUnsavedChangesController.current
    val guardedNavigate: (String) -> Unit = { id ->
        unsavedChanges?.request { onNavigate(id) } ?: onNavigate(id)
    }
    val guardedLogout = { unsavedChanges?.request(onLogout) ?: onLogout() }
    val guardedProfileClick = onProfileClick?.let { action ->
        { unsavedChanges?.request(action) ?: action() }
    }
    val account = user?.let { SidebarAccount(initial = it.initial, name = it.name, role = it.role) }
    val settingsRoute = sidebarItems.firstOrNull { it.label == pharmStrings.navSettings }?.id
    val helpRoute = sidebarItems.firstOrNull { it.label == pharmStrings.navHelp }?.id

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
    ) {

        PharmSidebar(
            activeId = currentRoute,
            onSelect = guardedNavigate,
            items = sidebarItems,
            collapsed = sidebarCollapsed,
            onToggleCollapse = if (sidebar.canCollapse) sidebar.toggle else null,
            status = if (pendingSyncCount > 0) {
                { collapsed ->
                    PendingSyncBadge(
                        count = pendingSyncCount,
                        onClick = onSyncClick,
                        iconOnly = collapsed,
                    )
                }
            } else {
                null
            },
            account = account,
            onProfileClick = guardedProfileClick,
            onSettingsClick = settingsRoute?.let { route -> { guardedNavigate(route) } },
            onHelpClick = helpRoute?.let { route -> { guardedNavigate(route) } },
            onLogout = guardedLogout,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars),
            ) { content() }
        }
    }
}

@Composable
private fun PendingSyncBadge(
    count: Int,
    onClick: () -> Unit,
    iconOnly: Boolean = false,
) {
    val t = pharmTokens
    val label = pharmStrings.commonPendingSyncBadge(count)
    val shape = if (iconOnly) t.shapes.pill else t.shapes.md
    Row(
        modifier = (if (iconOnly) Modifier.size(t.dimens.minimumTouchTarget) else Modifier)
            .clip(shape)
            .pharmClickable(role = androidx.compose.ui.semantics.Role.Button, shape = shape, onClick = onClick)
            .background(t.colors.dangerBg)
            .then(
                if (iconOnly) {
                    Modifier.semantics(mergeDescendants = true) { contentDescription = label }
                } else {
                    Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                },
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {
        Icon(
            imageVector = Icons.Outlined.CloudOff,
            contentDescription = null,
            tint = t.colors.dangerFg,
            modifier = Modifier.size(if (iconOnly) 18.dp else 14.dp),
        )
        if (!iconOnly) {
            Text(
                text = label,
                style = PharmText.badge.copy(color = t.colors.dangerFg),
            )
        }
    }
}
