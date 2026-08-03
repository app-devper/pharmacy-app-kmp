package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.LocalWindowSize
import app.devper.pharm.ui.components.WindowSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable

enum class PharmActionTone { Default, Primary, Success, Danger }

data class PharmAction(
    val label: String,
    val onClick: () -> Unit,
    val icon: ImageVector? = null,
    val tone: PharmActionTone = PharmActionTone.Default,
    val enabled: Boolean = true,
)

@Composable
fun PharmActionMenu(
    actions: List<PharmAction>,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    var expanded by remember { mutableStateOf(false) }
    var restoreFocusOnClose by remember { mutableStateOf(false) }
    val triggerFocus = remember { FocusRequester() }
    val useBottomSheet = usesActionBottomSheet(LocalWindowSize.current)

    LaunchedEffect(expanded) {
        if (!expanded && restoreFocusOnClose) {
            triggerFocus.requestFocus()
            restoreFocusOnClose = false
        }
    }

    fun dismiss() {
        restoreFocusOnClose = true
        expanded = false
    }

    val openMenuDesc = pharmStrings.commonOpenMenu
    Box(modifier = modifier) {
        PharmIconButton(
            contentDescription = openMenuDesc,
            onClick = { expanded = true },
            enabled = actions.isNotEmpty(),
            selected = expanded,
            minSize = pharmControlHeight,
            shape = t.shapes.pill,
            modifier = Modifier
                .focusRequester(triggerFocus)
                .sizeIn(
                    minWidth = pharmControlHeight,
                    minHeight = pharmControlHeight,
                ),
        ) {
            Icon(
                imageVector = PharmIcons.More,
                contentDescription = null,
                tint = if (expanded) t.colors.fg1 else t.colors.fg3,
                modifier = Modifier.size(18.dp),
            )
        }
        if (useBottomSheet) {
            if (expanded) {
                PharmActionBottomSheet(
                    actions = actions,
                    onDismissRequest = ::dismiss,
                    onAction = { action ->
                        dismiss()
                        if (action.enabled) action.onClick()
                    },
                )
            }
        } else {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = ::dismiss,
                shape = t.shapes.xl,
                containerColor = t.colors.surfaceRaised,
                tonalElevation = 0.dp,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, t.colors.borderSubtle),
            ) {
                PharmActionList(
                    actions = actions,
                    modifier = Modifier
                        .widthIn(min = 180.dp, max = 280.dp)
                        .padding(horizontal = 8.dp),
                    onDismissRequest = ::dismiss,
                    onAction = { action ->
                        dismiss()
                        if (action.enabled) action.onClick()
                    },
                )
            }
        }
    }
}

@Composable
private fun PharmActionBottomSheet(
    actions: List<PharmAction>,
    onDismissRequest: () -> Unit,
    onAction: (PharmAction) -> Unit,
) {
    val t = pharmTokens
    val title = pharmStrings.commonMenu

    PharmBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        Text(
            text = title,
            style = PharmText.h2,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                .semantics { heading() },
        )
        PharmActionList(
            actions = actions,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 16.dp),
            onDismissRequest = onDismissRequest,
            onAction = onAction,
        )
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun PharmActionList(
    actions: List<PharmAction>,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onAction: (PharmAction) -> Unit,
) {
    val enabled = actions.map { it.enabled }
    val itemFocusRequesters = remember(actions.size) { List(actions.size) { FocusRequester() } }
    var focusedIndex by remember { mutableStateOf(-1) }

    fun requestActionFocus(index: Int): Boolean {
        if (index !in itemFocusRequesters.indices) return false
        itemFocusRequesters[index].requestFocus()
        focusedIndex = index
        return true
    }

    LaunchedEffect(enabled) {
        requestActionFocus(
            actionFocusTargetIndex(
                enabled = enabled,
                currentIndex = -1,
                move = PharmActionFocusMove.First,
            ),
        )
    }

    Column(
        modifier = modifier.onPreviewKeyEvent { event ->
            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
            val move = when (event.key) {
                Key.DirectionDown -> PharmActionFocusMove.Next
                Key.DirectionUp -> PharmActionFocusMove.Previous
                Key.MoveHome -> PharmActionFocusMove.First
                Key.MoveEnd -> PharmActionFocusMove.Last
                Key.Escape -> {
                    onDismissRequest()
                    return@onPreviewKeyEvent true
                }
                else -> return@onPreviewKeyEvent false
            }
            requestActionFocus(
                actionFocusTargetIndex(
                    enabled = enabled,
                    currentIndex = focusedIndex,
                    move = move,
                ),
            )
        },
    ) {
        actions.forEachIndexed { index, action ->
            PharmActionRow(
                action = action,
                modifier = Modifier
                    .focusRequester(itemFocusRequesters[index])
                    .onFocusChanged { if (it.isFocused) focusedIndex = index },
                onClick = { onAction(action) },
            )
        }
    }
}

@Composable
private fun PharmActionRow(
    action: PharmAction,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val t = pharmTokens
    val fg = when (action.tone) {
        PharmActionTone.Default -> t.colors.fg1
        PharmActionTone.Primary -> t.colors.accent
        PharmActionTone.Success -> t.colors.successFg
        PharmActionTone.Danger  -> t.colors.dangerFg
    }
    val alpha = if (action.enabled) 1f else 0.4f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(t.dimens.actionMenuRowHeight)
            .clip(t.shapes.lg)
            .pharmClickable(enabled = action.enabled, shape = t.shapes.lg, onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (action.icon != null) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = fg.copy(alpha = alpha),
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = action.label,
            style = PharmText.body.copy(color = fg.copy(alpha = alpha)),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

internal fun usesActionBottomSheet(windowSize: WindowSize): Boolean = windowSize.isCompact

internal enum class PharmActionFocusMove { Next, Previous, First, Last }

internal fun actionFocusTargetIndex(
    enabled: List<Boolean>,
    currentIndex: Int,
    move: PharmActionFocusMove,
): Int = when (move) {
    PharmActionFocusMove.Next -> nextEnabledActionIndex(enabled, currentIndex, direction = 1)
    PharmActionFocusMove.Previous -> nextEnabledActionIndex(enabled, currentIndex, direction = -1)
    PharmActionFocusMove.First -> enabled.indexOfFirst { it }
    PharmActionFocusMove.Last -> enabled.indexOfLast { it }
}

internal fun nextEnabledActionIndex(
    enabled: List<Boolean>,
    currentIndex: Int,
    direction: Int,
): Int {
    if (enabled.none { it }) return -1
    val step = if (direction < 0) -1 else 1
    var index = if (currentIndex in enabled.indices) currentIndex else if (step > 0) -1 else 0
    repeat(enabled.size) {
        index = (index + step + enabled.size) % enabled.size
        if (enabled[index]) return index
    }
    return -1
}
