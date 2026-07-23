package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.common.pharmFocusRing
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

data class PharmTab(
    val id: String,
    val label: String,
    val count: Int? = null,
)

@Composable
fun PharmTabBar(
    tabs: List<PharmTab>,
    activeId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = true,
) {
    val t = pharmTokens
    val rowMod = if (fillMaxWidth) modifier.fillMaxWidth() else modifier
    val tabBarHeight = t.dimens.controlHeight + 8.dp
    BoxWithConstraints(
        modifier = rowMod
            .height(tabBarHeight)
            .background(t.colors.surface),
    ) {
        val scrollable = tabs.isNotEmpty() && maxWidth < 88.dp * tabs.size
        val listState = rememberLazyListState()
        LaunchedEffect(activeId, scrollable, tabs) {
            if (!scrollable) return@LaunchedEffect
            val activeIndex = tabs.indexOfFirst { it.id == activeId }
            if (activeIndex < 0) return@LaunchedEffect
            listState.scrollToItem(activeIndex)
        }
        if (scrollable) {
            LazyRow(
                state = listState,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items(items = tabs, key = { it.id }) { tab ->
                    PharmTabItem(
                        tab = tab,
                        active = tab.id == activeId,
                        onClick = { onSelect(tab.id) },
                        modifier = Modifier.widthIn(min = 88.dp),
                    )
                }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
            ) {
                tabs.forEach { tab ->
                    PharmTabItem(
                        tab = tab,
                        active = tab.id == activeId,
                        onClick = { onSelect(tab.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(t.colors.border)
                .align(Alignment.BottomStart),
        )
        if (scrollable && listState.canScrollBackward) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            listOf(t.colors.surface, t.colors.surface.copy(alpha = 0f)),
                        ),
                    )
                    .align(Alignment.CenterStart),
            )
        }
        if (scrollable && listState.canScrollForward) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            listOf(t.colors.surface.copy(alpha = 0f), t.colors.surface),
                        ),
                    )
                    .align(Alignment.CenterEnd),
            )
        }
    }
}

@Composable
private fun PharmTabItem(
    tab: PharmTab,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val bg = if (active) t.colors.accentBgSoft else androidx.compose.ui.graphics.Color.Transparent
    val fg = if (active) t.colors.accent else t.colors.fg2
    val interaction = remember { MutableInteractionSource() }
    val indication = LocalIndication.current

    Row(
        modifier = modifier
            .heightIn(min = t.dimens.controlHeight)
            .pharmFocusRing(interactionSource = interaction, shape = t.shapes.md)
            .clip(t.shapes.md)
            .background(bg)
            .selectable(
                selected = active,
                role = Role.Tab,
                onClick = onClick,
                interactionSource = interaction,
                indication = indication,
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = tab.label,
            style = PharmText.buttonMd.copy(
                color = fg,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (tab.count != null) {
            PharmBadge(
                text = tab.count.toString(),
                tone = if (active) PharmBadgeTone.Blue else PharmBadgeTone.Gray,
                size = PharmBadgeSize.Sm,
            )
        }
    }
}
