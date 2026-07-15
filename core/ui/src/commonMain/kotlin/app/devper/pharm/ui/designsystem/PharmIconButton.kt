package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.common.pharmClickable
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun PharmIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minSize: Dp = 48.dp,
    shape: Shape = pharmTokens.shapes.pill,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .sizeIn(minWidth = minSize, minHeight = minSize)
            .clip(shape)
            .pharmClickable(
                enabled = enabled,
                role = Role.Button,
                shape = shape,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
