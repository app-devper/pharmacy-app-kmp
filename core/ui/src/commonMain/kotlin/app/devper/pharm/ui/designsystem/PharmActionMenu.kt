package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
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

    val openMenuDesc = pharmStrings.commonOpenMenu
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .sizeIn(minWidth = t.dimens.controlHeight, minHeight = t.dimens.controlHeight)
                .clip(t.shapes.pill)
                .semantics(mergeDescendants = true) {
                    contentDescription = openMenuDesc
                    role = Role.Button
                }
                .pharmClickable(role = Role.Button, shape = t.shapes.pill) { expanded = true },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(t.shapes.pill)
                    .background(if (expanded) t.colors.borderSubtle else Color.Transparent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = PharmIcons.More,
                    contentDescription = null,
                    tint = if (expanded) t.colors.fg1 else t.colors.fg3,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(t.colors.surface),
        ) {
            Column(
                modifier = Modifier
                    .width(180.dp)
                    .padding(vertical = 4.dp),
            ) {
                actions.forEach { action ->
                    PharmActionRow(
                        action = action,
                        onClick = {
                            expanded = false
                            if (action.enabled) action.onClick()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun PharmActionRow(action: PharmAction, onClick: () -> Unit) {
    val t = pharmTokens
    val fg = when (action.tone) {
        PharmActionTone.Default -> t.colors.fg1
        PharmActionTone.Primary -> t.colors.accent
        PharmActionTone.Success -> t.colors.successFg
        PharmActionTone.Danger  -> t.colors.dangerFg
    }
    val alpha = if (action.enabled) 1f else 0.4f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(t.dimens.controlHeight)
            .pharmClickable(enabled = action.enabled, onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (action.icon != null) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = fg.copy(alpha = alpha),
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text = action.label,
            style = PharmText.bodySm.copy(color = fg.copy(alpha = alpha)),
        )
    }
}
