package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.components.CompactPageChrome
import app.devper.pharm.ui.components.LocalPageTitle
import app.devper.pharm.ui.components.LocalUnsavedChangesController
import app.devper.pharm.ui.components.RegisterCompactPageChrome
import app.devper.pharm.ui.components.LocalWindowSize
import app.devper.pharm.ui.components.PharmBreakpoint
import app.devper.pharm.ui.components.WindowSize

internal val pharmListToolbarCompactTopPadding = 12.dp
internal val pharmListToolbarCompactSectionSpacing = 32.dp
internal val pharmListToolbarDefaultSectionSpacing = 16.dp

internal fun listToolbarTopPadding(
    windowSize: WindowSize,
    expandedPadding: Dp,
): Dp = when (windowSize) {
    WindowSize.Compact,
    WindowSize.Medium,
    -> pharmListToolbarCompactTopPadding
    WindowSize.Expanded -> expandedPadding
}

internal fun usesCompactListToolbar(windowSize: WindowSize, availableWidth: Dp): Boolean =
    windowSize.isCompactShell || availableWidth < PharmBreakpoint.Medium

internal fun listToolbarSectionSpacing(
    compact: Boolean,
    hasSearch: Boolean,
    hasFilters: Boolean,
): Dp = if (compact && hasSearch && hasFilters) {
    pharmListToolbarCompactSectionSpacing
} else {
    pharmListToolbarDefaultSectionSpacing
}

internal fun hidesToolbarTitleForSearch(
    compact: Boolean,
    hasSearch: Boolean,
    availableWidth: Dp,
): Boolean = !compact && hasSearch && availableWidth < PharmBreakpoint.FormThreeCol

internal fun searchSharesRowWithActions(
    compact: Boolean,
    hasSearch: Boolean,
    hasInlineActions: Boolean,
): Boolean = compact && hasSearch && hasInlineActions

internal fun combinesCompactToolbarControls(
    compact: Boolean,
    hasFilters: Boolean,
    hasInlineActions: Boolean,
    allowSharedRow: Boolean = true,
): Boolean = compact && hasFilters && hasInlineActions && allowSharedRow

internal fun hasCompactToolbarContent(
    showTitle: Boolean,
    hasBack: Boolean,
    hasSearch: Boolean,
    hasFilters: Boolean,
    hasBadge: Boolean,
    hasInlineActions: Boolean,
): Boolean = showTitle || hasBack || hasSearch || hasFilters || hasBadge || hasInlineActions

internal fun movesSubpageHeaderToTopbar(windowSize: WindowSize, hasBack: Boolean): Boolean =
    windowSize.isCompactShell && hasBack

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PharmListToolbar(
    title: String = "",
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    searchValue: String? = null,
    onSearchChange: ((String) -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    searching: Boolean = false,
    searchPlaceholder: String = "",
    titleStyle: TextStyle = PharmText.h1,
    badge: (@Composable () -> Unit)? = null,
    filters: (@Composable FlowRowScope.() -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
    primaryAction: (@Composable () -> Unit)? = null,
    compactHeaderActions: Boolean = true,
    compactControlsSharedRow: Boolean = true,
) {
    val t = pharmTokens
    val windowSize = LocalWindowSize.current
    val gutter = pharmPageGutter
    val effectiveTitle = if (windowSize.isCompactShell && onBack == null) {
        ""
    } else {
        title.ifBlank { LocalPageTitle.current.takeUnless { windowSize.isCompact }.orEmpty() }
    }
    val topPadding = listToolbarTopPadding(
        windowSize = windowSize,
        expandedPadding = t.dimens.pageTopPaddingExpanded,
    )
    val unsavedChanges = LocalUnsavedChangesController.current
    val guardedBack = onBack?.let { action -> unsavedChanges?.guarded(action) ?: action }
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val compact = usesCompactListToolbar(windowSize, maxWidth)
        val showTitle = effectiveTitle.isNotEmpty()
        val placement = toolbarActionPlacement(windowSize, guardedBack != null, compactHeaderActions)
        val moveSubpageHeaderToTopbar = movesSubpageHeaderToTopbar(windowSize, guardedBack != null)
        val inlineActions: (@Composable () -> Unit)? = if (
            (actions != null && !placement.secondaryInTopbar) || (primaryAction != null && !placement.primaryInTopbar)
        ) {
            {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(t.spacing.s2),
                    verticalArrangement = Arrangement.spacedBy(t.spacing.s2),
                    itemVerticalAlignment = Alignment.CenterVertically,
                ) {
                    if (!placement.secondaryInTopbar) actions?.invoke()
                    if (!placement.primaryInTopbar) primaryAction?.invoke()
                }
            }
        } else null
        val topbarAction: (@Composable () -> Unit)? = if (
            (primaryAction != null && placement.primaryInTopbar) || (actions != null && placement.secondaryInTopbar)
        ) {
            {
                Row(horizontalArrangement = Arrangement.spacedBy(t.spacing.s2), verticalAlignment = Alignment.CenterVertically) {
                    if (placement.secondaryInTopbar) actions?.invoke()
                    if (placement.primaryInTopbar) primaryAction?.invoke()
                }
            }
        } else null
        val hasSearch = searchValue != null && onSearchChange != null
        val searchRowTakesActions = searchSharesRowWithActions(
            compact = compact,
            hasSearch = hasSearch,
            hasInlineActions = inlineActions != null,
        )
        val controlRowActions = inlineActions.takeUnless { searchRowTakesActions }
        val hideTitleForSearch = hidesToolbarTitleForSearch(
            compact = compact,
            hasSearch = hasSearch,
            availableWidth = maxWidth,
        )
        val combineCompactControls = combinesCompactToolbarControls(
            compact = compact,
            hasFilters = filters != null,
            hasInlineActions = controlRowActions != null,
            allowSharedRow = compactControlsSharedRow,
        )
        if (moveSubpageHeaderToTopbar && guardedBack != null) {
            RegisterCompactPageChrome(
                CompactPageChrome.Header(
                    title = effectiveTitle,
                    onBack = guardedBack,
                    actions = topbarAction,
                ),
            )
        } else if (topbarAction != null) {
            RegisterCompactPageChrome(CompactPageChrome.Actions(topbarAction))
        }
        val localShowTitle = showTitle && !moveSubpageHeaderToTopbar
        val localBack = guardedBack.takeUnless { moveSubpageHeaderToTopbar }
        val hasCompactContent = hasCompactToolbarContent(
            showTitle = localShowTitle,
            hasBack = localBack != null,
            hasSearch = hasSearch,
            hasFilters = filters != null,
            hasBadge = badge != null,
            hasInlineActions = inlineActions != null,
        )
        if (compact && !hasCompactContent) return@BoxWithConstraints
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = gutter, top = topPadding, end = gutter, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(
                    listToolbarSectionSpacing(
                        compact = compact,
                        hasSearch = searchValue != null,
                        hasFilters = filters != null,
                    ),
                ),
            ) {
                if (compact) {
                    if (localBack != null || localShowTitle) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (localBack != null) {
                                PharmIconButton(
                                    contentDescription = pharmStrings.commonBack,
                                    onClick = localBack,
                                    minSize = pharmControlHeight,
                                    modifier = Modifier.size(pharmControlHeight),
                                    shape = t.shapes.md,
                                ) {
                                    Icon(
                                        imageVector = PharmIcons.ReturnArrow,
                                        contentDescription = null,
                                        tint = t.colors.fg1,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                            if (localShowTitle) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = effectiveTitle,
                                        style = titleStyle,
                                        modifier = Modifier.semantics { heading() },
                                    )
                                    if (subtitle != null) {
                                        Text(
                                            text = subtitle,
                                            style = PharmText.micro.copy(color = t.colors.fgMuted),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (hasSearch && searchValue != null && onSearchChange != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PharmSearchField(
                                value = searchValue,
                                onValueChange = onSearchChange,
                                placeholder = searchPlaceholder,
                                onSearch = onSearch,
                                searching = searching,
                                modifier = Modifier.weight(1f),
                            )
                            if (searchRowTakesActions) {
                                CompositionLocalProvider(LocalCompactTopbarActions provides true) {
                                    inlineActions?.invoke()
                                }
                            }
                        }
                    }
                    if (combineCompactControls) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            FlowRow(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                itemVerticalAlignment = Alignment.CenterVertically,
                            ) {
                                badge?.invoke()
                                filters?.invoke(this)
                            }
                            CompositionLocalProvider(LocalCompactTopbarActions provides true) {
                                controlRowActions?.invoke()
                            }
                        }
                    } else if (filters != null || badge != null || controlRowActions != null) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            if (filters != null) {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    itemVerticalAlignment = Alignment.CenterVertically,
                                    content = filters,
                                )
                            }
                            if (badge != null || controlRowActions != null) {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (badge == null) {
                                        Arrangement.spacedBy(8.dp, Alignment.End)
                                    } else {
                                        Arrangement.spacedBy(8.dp)
                                    },
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    itemVerticalAlignment = Alignment.CenterVertically,
                                ) {
                                    badge?.invoke()
                                    CompositionLocalProvider(LocalCompactTopbarActions provides true) {
                                        controlRowActions?.invoke()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        itemVerticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (guardedBack != null) {
                            PharmIconButton(
                                contentDescription = pharmStrings.commonBack,
                                onClick = guardedBack,
                                minSize = pharmControlHeight,
                                modifier = Modifier
                                    .size(pharmControlHeight),
                                shape = t.shapes.md,
                            ) {
                                Icon(
                                    imageVector = PharmIcons.ReturnArrow,
                                    contentDescription = null,
                                    tint = t.colors.fg1,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                        if (showTitle && !hideTitleForSearch) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = effectiveTitle,
                                    style = titleStyle,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.semantics { heading() },
                                )
                                if (subtitle != null) {
                                    Text(
                                        text = subtitle,
                                        style = PharmText.micro.copy(color = t.colors.fgMuted),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        badge?.invoke()
                        if (searchValue != null && onSearchChange != null) {
                            Box(modifier = Modifier.width(t.dimens.searchFieldWidth)) {
                                PharmSearchField(
                                    value = searchValue,
                                    onValueChange = onSearchChange,
                                    placeholder = searchPlaceholder,
                                    onSearch = onSearch,
                                    searching = searching,
                                )
                            }
                        }
                        inlineActions?.invoke()
                    }
                }
                if (!compact && filters != null) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        itemVerticalAlignment = Alignment.CenterVertically,
                        content = filters,
                    )
                }
            }
            if (compact) {
                PharmDivider()
            }
        }
    }
}
