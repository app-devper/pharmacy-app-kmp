package app.devper.pharm.ui.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.pharmTokens

enum class PharmDensity(val rowHeight: Dp, val headerHeight: Dp) {
    Comfortable(rowHeight = 52.dp, headerHeight = 44.dp),
    Compact(rowHeight = 44.dp, headerHeight = 40.dp),
}

val LocalPharmDensity = staticCompositionLocalOf { PharmDensity.Comfortable }

internal val pharmControlHeight: Dp
    @Composable get() = pharmTokens.dimens.compactControlHeight
