package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.components.LocalWindowSize

@Composable
fun PharmToolbarMenu(
    actions: List<PharmAction>,
    promotedAction: PharmAction?,
    modifier: Modifier = Modifier,
) {
    val compact = LocalWindowSize.current.isCompactShell
    val spacing = pharmTokens.spacing.s2
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(spacing), verticalAlignment = Alignment.CenterVertically) {
        PharmActionMenu(actions = if (compact) actions + listOfNotNull(promotedAction) else actions)
        if (!compact && promotedAction != null) {
            PharmButton(
                label = promotedAction.label,
                onClick = promotedAction.onClick,
                enabled = promotedAction.enabled,
                variant = PharmButtonVariant.Outline,
                size = PharmButtonSize.Sm,
                leadingIcon = { promotedAction.icon?.let { Icon(it, contentDescription = null) } },
            )
        }
    }
}
