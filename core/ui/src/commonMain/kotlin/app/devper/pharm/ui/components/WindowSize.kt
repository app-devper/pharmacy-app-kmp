package app.devper.pharm.ui.components

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object PharmBreakpoint {
    val Stack = 360.dp
    val FormTwoCol = 560.dp
    val Medium = 600.dp
    val FormThreeCol = 720.dp
    val Expanded = 840.dp
    val DashboardCap = 1000.dp
    val GridWide = 1280.dp
}

enum class WindowSize {
    Compact,
    Medium,
    Expanded;

    val isCompact: Boolean get() = this == Compact
    val isAtLeastMedium: Boolean get() = this != Compact

    companion object {
        fun fromWidth(width: Dp): WindowSize = when {
            width < PharmBreakpoint.Medium -> Compact
            width < PharmBreakpoint.Expanded -> Medium
            else -> Expanded
        }
    }
}
