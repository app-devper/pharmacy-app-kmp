package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable

@Composable
fun PharmListCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    highlighted: Boolean = false,
    leading: (@Composable () -> Unit)? = null,
    status: (@Composable RowScope.() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    body: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val t = pharmTokens
    val shape = t.shapes.lg
    val borderColor = if (highlighted) t.colors.successFg else t.colors.border

    val surface = modifier
        .fillMaxWidth()
        .clip(shape)
        .background(t.colors.surface, shape)
        .border(1.dp, borderColor, shape)
        .let { if (onClick != null) it.pharmClickable(role = Role.Button, shape = shape, onClick = onClick) else it }

    Row(
        modifier = surface.padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (leading != null) leading()

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = PharmText.body.copy(color = t.colors.fg1, fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = PharmText.micro.copy(color = t.colors.fgMuted),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (status != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    status()
                }
            }
            body?.invoke(this)
        }

        if (trailing != null) trailing()
    }
}
