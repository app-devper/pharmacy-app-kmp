package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable

private val SidebarRailWidth: Dp = 52.dp

data class SidebarNavItem(
    val id: String,
    val icon: ImageVector,
    val admin: Boolean = false,
    val pinned: Boolean = false,
    val label: String = "",
    val sectionLabel: String = "",
)

data class SidebarAccount(
    val initial: String,
    val name: String,
    val role: String,
)

fun SidebarNavItem.displayLabel(s: PharmStrings): String = label.ifBlank { fallbackLabel(s) }

private fun SidebarNavItem.fallbackLabel(s: PharmStrings): String = when (id) {
    "sell" -> s.navSell
    "salesHistory" -> s.navSalesHistory
    "stock" -> s.navStock
    "stockCount" -> s.navStockCounts
    "expiry" -> s.navExpiry
    "movements" -> s.navMovements
    "offlineSync" -> s.navOfflineSync
    "imports" -> s.navImports
    "suppliers" -> s.navSuppliers
    "customers" -> s.navCustomers
    "reports" -> s.navReports
    "profit" -> s.navProfit
    "kyforms" -> s.navKyForms
    "users" -> s.navUsers
    "settings" -> s.navSettings
    "help" -> s.navHelp
    else -> id
}

val DefaultPharmNav: List<SidebarNavItem> = listOf(
    SidebarNavItem("sell",         PharmIcons.Sell, pinned = true),
    SidebarNavItem("salesHistory", PharmIcons.SalesHistory),
    SidebarNavItem("stock",        PharmIcons.Stock),
    SidebarNavItem("stockCount",   PharmIcons.StockCount,      admin = true),
    SidebarNavItem("expiry",       PharmIcons.Expiry,  admin = true),
    SidebarNavItem("movements",    PharmIcons.Movements),
    SidebarNavItem("offlineSync",  PharmIcons.OfflineSync),
    SidebarNavItem("imports",      PharmIcons.Imports,      admin = true),
    SidebarNavItem("suppliers",    PharmIcons.Suppliers,      admin = true),
    SidebarNavItem("customers",    PharmIcons.Customers),
    SidebarNavItem("reports",      PharmIcons.Reports),
    SidebarNavItem("profit",       PharmIcons.Profit,              admin = true),
    SidebarNavItem("kyforms",      PharmIcons.KyForms, admin = true),
    SidebarNavItem("users",        PharmIcons.Users,   admin = true),
    SidebarNavItem("settings",     PharmIcons.Settings,       admin = true),
    SidebarNavItem("help",         PharmIcons.Help),
)

@Composable
fun PharmSidebar(
    activeId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    items: List<SidebarNavItem> = DefaultPharmNav,
    collapsed: Boolean = false,
    expandedWidth: Dp = pharmTokens.dimens.sidebarWidth,
    applySystemInsets: Boolean = false,
    onToggleCollapse: (() -> Unit)? = null,
    status: (@Composable (Boolean) -> Unit)? = null,
    account: SidebarAccount? = null,
    onProfileClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    onHelpClick: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
) {
    val t = pharmTokens
    val borderColor = t.colors.border
    val reducedMotion = LocalReducedMotion.current
    val headerHeight = t.dimens.sidebarHeaderHeight
    val rowHeight = t.dimens.sidebarRowHeight
    val listPadding = t.spacing.s1
    val pinnedItems = remember(items) { items.filter(SidebarNavItem::pinned) }
    val scrollableItems = remember(items) { items.filterNot(SidebarNavItem::pinned) }
    val width by animateDpAsState(
        targetValue = sidebarTargetWidth(collapsed = collapsed, expandedWidth = expandedWidth),
        animationSpec = if (reducedMotion) snap() else tween(PharmMotion.Medium),
        label = "sidebarWidth",
    )
    Column(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .background(t.colors.sidebarBg)
            .drawBehind {
                val w = 1.dp.toPx()
                drawRect(color = borderColor, topLeft = Offset(size.width - w, 0f), size = Size(w, size.height))
            }
            .then(
                if (applySystemInsets) {
                    Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                } else {
                    Modifier
                },
            ),
    ) {

        BrandHeader(
            collapsed = collapsed,
            height = headerHeight,
            onToggleCollapse = onToggleCollapse,
        )

        if (pinnedItems.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = listPadding),
                verticalArrangement = Arrangement.spacedBy(t.spacing.s0_5),
            ) {
                pinnedItems.forEach { item ->
                    SidebarRow(
                        item = item,
                        active = item.id == activeId,
                        collapsed = collapsed,
                        height = rowHeight,
                        onClick = { onSelect(item.id) },
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(
                    top = if (pinnedItems.isEmpty()) listPadding else t.spacing.s0_5,
                    bottom = listPadding,
                ),
            verticalArrangement = Arrangement.spacedBy(t.spacing.s0_5),
        ) {
            scrollableItems.forEach { item ->
                item(key = item.id) {
                    SidebarRow(
                        item = item,
                        active = item.id == activeId,
                        collapsed = collapsed,
                        height = rowHeight,
                        onClick = { onSelect(item.id) },
                    )
                }
            }
        }

        if (status != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                status(collapsed)
            }
        }

        if (account != null) {
            SidebarAccountDock(
                account = account,
                collapsed = collapsed,
                menuWidth = sidebarAccountMenuWidth(expandedWidth),
                onProfileClick = onProfileClick,
                onSettingsClick = onSettingsClick,
                onHelpClick = onHelpClick,
                onLogout = onLogout,
            )
        }
    }
}

internal fun sidebarTargetWidth(collapsed: Boolean, expandedWidth: Dp): Dp =
    if (collapsed) SidebarRailWidth else expandedWidth

@Composable
private fun BrandHeader(
    collapsed: Boolean,
    height: Dp,
    onToggleCollapse: (() -> Unit)?,
) {
    val t = pharmTokens
    val toggleLabel = if (collapsed) pharmStrings.commonExpandMenu else pharmStrings.commonCollapseMenu
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = if (collapsed) 4.dp else 16.dp),
        horizontalArrangement = if (collapsed) Arrangement.Center else Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (collapsed) {
            if (onToggleCollapse != null) {
                PharmIconButton(
                    contentDescription = toggleLabel,
                    onClick = onToggleCollapse,
                    minSize = t.dimens.minimumTouchTarget,
                    shape = t.shapes.md,
                    modifier = Modifier.size(t.dimens.minimumTouchTarget),
                ) {
                    Icon(
                        imageVector = PharmIcons.SidebarPanel,
                        contentDescription = null,
                        tint = t.colors.sidebarFg,
                        modifier = Modifier.size(20.dp),
                    )
                }
            } else {
                Icon(
                    imageVector = PharmIcons.Stock,
                    contentDescription = pharmStrings.commonAppBrand,
                    tint = t.colors.sidebarFg,
                    modifier = Modifier.size(20.dp),
                )
            }
        } else {
            Text(
                text = pharmStrings.commonAppBrand,
                style = PharmText.h2.copy(
                    color = t.colors.sidebarFg,
                    fontWeight = FontWeight.SemiBold,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (onToggleCollapse != null) {
                PharmIconButton(
                    contentDescription = toggleLabel,
                    onClick = onToggleCollapse,
                    minSize = t.dimens.minimumTouchTarget,
                    shape = t.shapes.md,
                    modifier = Modifier.size(t.dimens.minimumTouchTarget),
                ) {
                    Icon(
                        imageVector = PharmIcons.SidebarPanel,
                        contentDescription = null,
                        tint = t.colors.sidebarFgMuted,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarRow(
    item: SidebarNavItem,
    active: Boolean,
    collapsed: Boolean,
    height: Dp,
    onClick: () -> Unit,
) {
    val t = pharmTokens
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val reducedMotion = LocalReducedMotion.current
    val bg by animateColorAsState(
        targetValue = when {
            active -> t.colors.sidebarItemActive
            hovered -> t.colors.sidebarItemHover
            else -> t.colors.sidebarBg
        },
        animationSpec = if (reducedMotion) snap() else tween(PharmMotion.Fast),
        label = "sidebarRowBackground",
    )
    val fg = t.colors.sidebarFg
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(t.shapes.md)
            .background(bg)
            .pharmClickable(
                role = Role.Button,
                shape = t.shapes.md,
                interactionSource = interaction,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                role = Role.Button
                selected = active
            }
            .height(height)
            .padding(horizontal = if (collapsed) 0.dp else 12.dp),
        horizontalArrangement = if (collapsed) Arrangement.Center else Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = if (collapsed) item.displayLabel(pharmStrings) else null,
            tint = fg,
            modifier = Modifier.size(18.dp),
        )
        if (!collapsed) {
            Text(
                text = item.displayLabel(pharmStrings),
                style = PharmText.body.copy(
                    color = fg,
                    fontWeight = if (active) FontWeight.Medium else FontWeight.Normal,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private data class SidebarAccountMenuItem(
    val label: String,
    val icon: ImageVector,
    val showTrailingChevron: Boolean = false,
    val onClick: () -> Unit,
)

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun SidebarAccountDock(
    account: SidebarAccount,
    collapsed: Boolean,
    menuWidth: Dp,
    onProfileClick: (() -> Unit)?,
    onSettingsClick: (() -> Unit)?,
    onHelpClick: (() -> Unit)?,
    onLogout: (() -> Unit)?,
) {
    val t = pharmTokens
    var expanded by remember { mutableStateOf(false) }
    var restoreFocusOnClose by remember { mutableStateOf(false) }
    var focusedIndex by remember { mutableStateOf(-1) }
    val triggerFocus = remember { FocusRequester() }
    val primaryActions = listOfNotNull(
        onProfileClick?.let {
            SidebarAccountMenuItem(
                label = pharmStrings.profileTitle,
                icon = PharmIcons.Person,
                onClick = it,
            )
        },
        onSettingsClick?.let {
            SidebarAccountMenuItem(
                label = pharmStrings.navSettings,
                icon = PharmIcons.Settings,
                onClick = it,
            )
        },
    )
    val secondaryActions = listOfNotNull(
        onHelpClick?.let {
            SidebarAccountMenuItem(
                label = pharmStrings.commonHelp,
                icon = PharmIcons.Help,
                showTrailingChevron = true,
                onClick = it,
            )
        },
        onLogout?.let {
            SidebarAccountMenuItem(
                label = pharmStrings.commonLogout,
                icon = PharmIcons.Logout,
                showTrailingChevron = true,
                onClick = it,
            )
        },
    )
    val actions = primaryActions + secondaryActions
    val summaryFocusOffset = if (onProfileClick != null) 1 else 0
    val focusCount = summaryFocusOffset + actions.size
    val focusRequesters = remember(focusCount) { List(focusCount) { FocusRequester() } }

    fun requestMenuFocus(index: Int): Boolean {
        if (index !in focusRequesters.indices) return false
        runCatching { focusRequesters[index].requestFocus() }
        focusedIndex = index
        return true
    }

    fun dismiss() {
        restoreFocusOnClose = true
        expanded = false
    }

    LaunchedEffect(expanded, focusCount) {
        if (expanded) {
            requestMenuFocus(0)
        } else if (restoreFocusOnClose) {
            runCatching { triggerFocus.requestFocus() }
            restoreFocusOnClose = false
            focusedIndex = -1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = ::dismiss,
            offset = DpOffset(0.dp, (-8).dp),
            shape = t.shapes.xl,
            containerColor = t.colors.surfaceRaised,
            tonalElevation = 0.dp,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, t.colors.borderSubtle),
            modifier = Modifier
                .width(menuWidth)
                .padding(horizontal = 8.dp)
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    val move = when (event.key) {
                        Key.DirectionDown -> PharmActionFocusMove.Next
                        Key.DirectionUp -> PharmActionFocusMove.Previous
                        Key.MoveHome -> PharmActionFocusMove.First
                        Key.MoveEnd -> PharmActionFocusMove.Last
                        Key.Escape -> {
                            dismiss()
                            return@onPreviewKeyEvent true
                        }
                        else -> return@onPreviewKeyEvent false
                    }
                    requestMenuFocus(
                        actionFocusTargetIndex(
                            enabled = List(focusCount) { true },
                            currentIndex = focusedIndex,
                            move = move,
                        ),
                    )
                },
        ) {
            SidebarAccountSummary(
                account = account,
                onClick = onProfileClick?.let { action ->
                    {
                        dismiss()
                        action()
                    }
                },
                modifier = if (onProfileClick != null) {
                    Modifier
                        .focusRequester(focusRequesters.first())
                        .onFocusChanged { if (it.isFocused) focusedIndex = 0 }
                } else {
                    Modifier
                },
            )
            SidebarAccountDivider()
            actions.forEachIndexed { index, action ->
                if (index == primaryActions.size && secondaryActions.isNotEmpty() && primaryActions.isNotEmpty()) {
                    SidebarAccountDivider()
                }
                SidebarAccountAction(
                    label = action.label,
                    icon = action.icon,
                    showTrailingChevron = action.showTrailingChevron,
                    modifier = Modifier
                        .focusRequester(focusRequesters[index + summaryFocusOffset])
                        .onFocusChanged {
                            if (it.isFocused) focusedIndex = index + summaryFocusOffset
                        },
                    onClick = {
                        dismiss()
                        action.onClick()
                    },
                )
            }
        }
        SidebarAccountTrigger(
            account = account,
            collapsed = collapsed,
            expanded = expanded,
            onClick = {
                if (expanded) dismiss() else expanded = true
            },
            modifier = Modifier.focusRequester(triggerFocus),
        )
    }
}

internal fun sidebarAccountMenuWidth(sidebarWidth: Dp): Dp =
    (sidebarWidth - 16.dp).coerceIn(244.dp, 344.dp)

@Composable
private fun SidebarAccountTrigger(
    account: SidebarAccount,
    collapsed: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val reducedMotion = LocalReducedMotion.current
    val menuDescription = if (expanded) pharmStrings.commonCloseAccountMenu else pharmStrings.commonOpenAccountMenu
    val bg by animateColorAsState(
        targetValue = if (expanded || hovered) t.colors.sidebarItemActive else t.colors.sidebarBg,
        animationSpec = if (reducedMotion) snap() else tween(PharmMotion.Fast),
        label = "sidebarAccountBackground",
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(bg)
            .pharmClickable(
                role = Role.Button,
                shape = t.shapes.md,
                interactionSource = interaction,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = menuDescription
            }
            .height(t.dimens.accountSummaryHeight)
            .padding(horizontal = if (collapsed) 0.dp else 12.dp),
        horizontalArrangement = if (collapsed) Arrangement.Center else Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PharmAvatarCircle(text = account.initial, size = PharmAvatarSize.Sm, tone = PharmBadgeTone.Green)
        if (!collapsed) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.name,
                    style = PharmText.body.copy(color = t.colors.sidebarFg, fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = account.role,
                    style = PharmText.micro.copy(color = t.colors.sidebarFgMuted),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = PharmIcons.ChevronRight,
                contentDescription = null,
                tint = t.colors.sidebarFgMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun SidebarAccountSummary(
    account: SidebarAccount,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val reducedMotion = LocalReducedMotion.current
    val bg by animateColorAsState(
        targetValue = if (hovered && onClick != null) t.colors.hoverSurfaceRaised else t.colors.surfaceRaised,
        animationSpec = if (reducedMotion) snap() else tween(PharmMotion.Fast),
        label = "sidebarAccountSummaryBackground",
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(bg)
            .then(
                if (onClick != null) {
                    Modifier.pharmClickable(
                        role = Role.Button,
                        shape = t.shapes.md,
                        interactionSource = interaction,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .height(t.dimens.accountSummaryHeight)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PharmAvatarCircle(text = account.initial, size = PharmAvatarSize.Sm, tone = PharmBadgeTone.Green)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = account.name,
                style = PharmText.body.copy(color = t.colors.fg1, fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = account.role,
                style = PharmText.meta.copy(color = t.colors.fgMuted),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (onClick != null) {
            Icon(
                imageVector = PharmIcons.ChevronRight,
                contentDescription = null,
                tint = t.colors.fgMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun SidebarAccountDivider() {
    val t = pharmTokens
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(1.dp)
            .background(t.colors.divider),
    )
}

@Composable
private fun SidebarAccountAction(
    label: String,
    icon: ImageVector,
    showTrailingChevron: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val reducedMotion = LocalReducedMotion.current
    val bg by animateColorAsState(
        targetValue = if (hovered) t.colors.hoverSurfaceRaised else t.colors.surfaceRaised,
        animationSpec = if (reducedMotion) snap() else tween(PharmMotion.Fast),
        label = "sidebarAccountActionBackground",
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(bg)
            .pharmClickable(
                role = Role.Button,
                shape = t.shapes.md,
                interactionSource = interaction,
                onClick = onClick,
            )
            .height(t.dimens.actionMenuRowHeight)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = t.colors.fg2,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label,
            style = PharmText.body.copy(color = t.colors.fg1),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (showTrailingChevron) {
            Icon(
                imageVector = PharmIcons.ChevronRight,
                contentDescription = null,
                tint = t.colors.fgMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
