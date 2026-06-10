package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

data class PharmFilterChip(
    val id: String,
    val label: String,
    val icon: ImageVector? = null,
    val count: Int? = null,
)

@Composable
fun PharmFilterChips(
    chips: List<PharmFilterChip>,
    activeIds: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    role: Role = Role.Checkbox,
) {
    val scrollState = rememberScrollState()
    val rowMod = if (scrollable) modifier.horizontalScroll(scrollState) else modifier
    Row(
        modifier = rowMod.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        chips.forEach { chip ->
            PharmFilterChipItem(
                chip = chip,
                active = chip.id in activeIds,
                onClick = { onToggle(chip.id) },
                role = role,
            )
        }
    }
}

@Composable
fun PharmSingleSelectChips(
    chips: List<PharmFilterChip>,
    activeId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
) {
    PharmFilterChips(
        chips = chips,
        activeIds = if (activeId != null) setOf(activeId) else emptySet(),
        onToggle = onSelect,
        modifier = modifier,
        scrollable = scrollable,
        role = Role.RadioButton,
    )
}

@Composable
private fun PharmFilterChipItem(
    chip: PharmFilterChip,
    active: Boolean,
    onClick: () -> Unit,
    role: Role,
) {
    val t = pharmTokens
    val bg = if (active) t.colors.surface else t.colors.bgPage
    val fg = if (active) t.colors.accent else t.colors.fg2
    val border = if (active) t.colors.accent else t.colors.border

    Row(
        modifier = Modifier
            .heightIn(min = t.dimens.controlHeight)
            .clip(t.shapes.pill)
            .background(bg)
            .border(1.dp, border, t.shapes.pill)
            .selectable(selected = active, role = role, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (chip.icon != null) {
            Icon(
                imageVector = chip.icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.padding(end = 0.dp).then(Modifier),
            )
        }
        Text(
            text = chip.label,
            style = PharmText.bodySm.copy(
                color = fg,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
            ),
        )
        if (chip.count != null) {
            Text(
                text = chip.count.toString(),
                style = PharmText.micro.copy(
                    color = fg,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}
