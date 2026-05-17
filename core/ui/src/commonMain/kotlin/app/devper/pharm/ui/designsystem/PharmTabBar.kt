package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    Box(
        modifier = rowMod
            .background(t.colors.surface),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
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

    Row(
        modifier = modifier
            .clip(t.shapes.md)
            .background(bg)
            .clickable(onClick = onClick)
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
