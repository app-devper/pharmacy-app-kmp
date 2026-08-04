package app.devper.pharm.ui.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
    val rowMod = if (fillMaxWidth) modifier.fillMaxWidth() else modifier
    val controlHeight = pharmControlHeight
    val tabBarHeight = controlHeight + 8.dp
    BoxWithConstraints(
        modifier = rowMod
            .height(tabBarHeight),
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
                        controlHeight = controlHeight,
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
                        controlHeight = controlHeight,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun PharmTabItem(
    tab: PharmTab,
    active: Boolean,
    onClick: () -> Unit,
    controlHeight: Dp,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val fg = if (active) t.colors.fg1 else t.colors.fg2
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val reducedMotion = LocalReducedMotion.current
    val bg by animateColorAsState(
        targetValue = toggleSurface(active = active, hovered = hovered, colors = t.colors),
        animationSpec = if (reducedMotion) snap() else tween(PharmMotion.Fast),
        label = "pharmTabBackground",
    )
    val indication = LocalIndication.current

    Row(
        modifier = modifier
            .heightIn(min = controlHeight)
            .pharmFocusRing(interactionSource = interaction, shape = t.shapes.pill)
            .clip(t.shapes.pill)
            .background(bg)
            .selectable(
                selected = active,
                role = Role.Tab,
                onClick = onClick,
                interactionSource = interaction,
                indication = indication,
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
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
                tone = PharmBadgeTone.Gray,
                size = PharmBadgeSize.Sm,
            )
        }
    }
}
