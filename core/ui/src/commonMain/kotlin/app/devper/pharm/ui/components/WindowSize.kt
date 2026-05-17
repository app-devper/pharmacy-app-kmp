package app.devper.pharm.ui.components

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSize {
    Compact,
    Medium,
    Expanded;

    val isCompact: Boolean get() = this == Compact
    val isAtLeastMedium: Boolean get() = this != Compact

    companion object {
        fun fromWidth(width: Dp): WindowSize = when {
            width < 600.dp -> Compact
            width < 840.dp -> Medium
            else -> Expanded
        }
    }
}
