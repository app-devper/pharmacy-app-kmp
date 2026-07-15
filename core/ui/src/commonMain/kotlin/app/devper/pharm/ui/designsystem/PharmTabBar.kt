package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    BoxWithConstraints(
        modifier = rowMod
            .background(t.colors.surface),
    ) {
        val scrollable = tabs.isNotEmpty() && maxWidth < 88.dp * tabs.size
        val scrollState = rememberScrollState()
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .then(if (scrollable) Modifier.horizontalScroll(scrollState) else Modifier.fillMaxWidth())
                .padding(horizontal = 4.dp, vertical = 4.dp),
        ) {
            tabs.forEach { tab ->
                PharmTabItem(
                    tab = tab,
                    active = tab.id == activeId,
                    onClick = { onSelect(tab.id) },
                    modifier = if (scrollable) Modifier.widthIn(min = 88.dp) else Modifier.weight(1f),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(t.colors.border)
                .align(Alignment.BottomStart),
        )
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
