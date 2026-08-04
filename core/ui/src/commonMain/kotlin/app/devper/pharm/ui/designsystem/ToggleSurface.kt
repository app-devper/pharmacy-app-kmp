package app.devper.pharm.ui.designsystem

import androidx.compose.ui.graphics.Color
import app.devper.pharm.ui.theme.PharmColors

fun toggleSurface(active: Boolean, hovered: Boolean, colors: PharmColors): Color = when {
    active -> colors.selectedSurface
    hovered -> colors.hoverSurface
    else -> Color.Transparent
}

fun toggleBorder(active: Boolean, colors: PharmColors): Color =
    if (active) colors.border else Color.Transparent
