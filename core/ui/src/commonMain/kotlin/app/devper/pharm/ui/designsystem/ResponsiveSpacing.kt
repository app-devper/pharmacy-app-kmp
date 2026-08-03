package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.LocalWindowSize
import app.devper.pharm.ui.components.WindowSize
import app.devper.pharm.ui.theme.pharmTokens

fun pageHorizontalGutter(windowSize: WindowSize): Dp =
    if (windowSize.isCompactContent) 16.dp else 24.dp

val pharmPageGutter: Dp
    @Composable get() = pageHorizontalGutter(LocalWindowSize.current)

@Composable
fun Modifier.pharmFormContentPadding(vertical: Dp = 16.dp): Modifier = padding(
    horizontal = pharmPageGutter,
    vertical = vertical,
)

@Composable
fun ColumnScope.pharmFormContentWidth(): Modifier = Modifier
    .align(Alignment.CenterHorizontally)
    .widthIn(max = pharmTokens.dimens.formContentMaxWidth)
    .fillMaxWidth()
