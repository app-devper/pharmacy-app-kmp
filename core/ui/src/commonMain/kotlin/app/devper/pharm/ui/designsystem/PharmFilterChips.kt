package app.devper.pharm.ui.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmFocusRing

data class PharmFilterChip(
    val id: String,
    val label: String,
    val icon: ImageVector? = null,
    val count: Int? = null,
)

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun PharmFilterChips(
    chips: List<PharmFilterChip>,
    activeIds: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    role: Role = Role.Checkbox,
) {
    val scrollState = rememberScrollState()
    val groupModifier = modifier.selectableGroup()
    if (scrollable) {
        Row(
            modifier = groupModifier.horizontalScroll(scrollState),
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
    } else {
        FlowRow(
            modifier = groupModifier,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
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
    val fg = if (active) t.colors.fg1 else t.colors.fg2
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val reducedMotion = LocalReducedMotion.current
    val bg by animateColorAsState(
        targetValue = toggleSurface(active = active, hovered = hovered, colors = t.colors),
        animationSpec = if (reducedMotion) snap() else tween(PharmMotion.Fast),
        label = "pharmFilterChipBackground",
    )
    val borderColor by animateColorAsState(
        targetValue = toggleBorder(active = active, colors = t.colors),
        animationSpec = if (reducedMotion) snap() else tween(PharmMotion.Fast),
        label = "pharmFilterChipBorder",
    )

    Row(
        modifier = Modifier
            .heightIn(min = pharmControlHeight)
            .clip(t.shapes.pill)
            .background(bg)
            .border(1.dp, borderColor, t.shapes.pill)
            .pharmFocusRing(interactionSource = interaction, shape = t.shapes.pill)
            .selectable(
                selected = active,
                role = role,
                onClick = onClick,
                interactionSource = interaction,
                indication = LocalIndication.current,
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (chip.icon != null) {
            Icon(
                imageVector = chip.icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(18.dp),
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
