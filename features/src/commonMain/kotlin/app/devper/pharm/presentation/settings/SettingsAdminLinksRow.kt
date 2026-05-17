package app.devper.pharm.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SettingsMenuSection(state: SettingsEditorUiState, nav: SettingsNavCallbacks) {
    val groups = state.menuGroups
    if (groups.isEmpty()) return

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            groups.forEach { (group, items) ->
                val collapsed = group in state.collapsedGroups
                SettingsGroupHeader(
                    label = group.label,
                    count = items.size,
                    collapsed = collapsed,
                    onToggle = { nav.onToggleGroup(group) },
                )
                if (!collapsed) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items.forEach { item ->
                            AdminLink(label = "${item.label} →", onClick = { nav.onNavigate(item.key) })
                        }
                    }
                }
                Spacer(modifier = Modifier.size(4.dp))
            }
        }
    }
}

@Composable
private fun SettingsGroupHeader(
    label: String,
    count: Int,
    collapsed: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (collapsed) Icons.AutoMirrored.Filled.KeyboardArrowRight else Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Box(modifier = Modifier.padding(end = 4.dp)) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AdminLink(label: String, onClick: () -> Unit) {
    PharmButton(
        label = label,
        onClick = onClick,
        variant = PharmButtonVariant.Ghost,
        size = PharmButtonSize.Sm,
    )
}
