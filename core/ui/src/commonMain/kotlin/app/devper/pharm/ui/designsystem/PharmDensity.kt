package app.devper.pharm.ui.designsystem

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class PharmDensity(val rowHeight: Dp, val headerHeight: Dp) {
    Comfortable(rowHeight = 48.dp, headerHeight = 36.dp),
    Compact(rowHeight = 36.dp, headerHeight = 30.dp),
}

val LocalPharmDensity = staticCompositionLocalOf { PharmDensity.Comfortable }
