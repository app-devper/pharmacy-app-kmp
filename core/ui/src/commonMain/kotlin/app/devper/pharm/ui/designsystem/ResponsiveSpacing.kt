package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.LocalWindowSize
import app.devper.pharm.ui.components.WindowSize

fun formContentHorizontalPadding(windowSize: WindowSize): Dp =
    if (windowSize.isCompact) 16.dp else 24.dp

@Composable
fun Modifier.pharmFormContentPadding(vertical: Dp = 16.dp): Modifier = padding(
    horizontal = formContentHorizontalPadding(LocalWindowSize.current),
    vertical = vertical,
)
