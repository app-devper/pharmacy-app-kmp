package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

data class BottomNavItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun PharmBottomNav(
    items: List<BottomNavItem>,
    activeId: String,
    onSelect: (String) -> Unit,
    moreLabel: String,
    moreIcon: ImageVector,
    onMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    Column(modifier = modifier.fillMaxWidth().background(t.colors.surface)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(t.colors.border),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                BottomNavCell(
                    icon = item.icon,
                    label = item.label,
                    active = item.id == activeId,
                    onClick = { onSelect(item.id) },
                    modifier = Modifier.weight(1f),
                )
            }
            BottomNavCell(
                icon = moreIcon,
                label = moreLabel,
                active = false,
                onClick = onMore,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BottomNavCell(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val tint = if (active) t.colors.accent else t.colors.fgMuted
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(t.shapes.sm)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = PharmText.micro.copy(color = tint),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
