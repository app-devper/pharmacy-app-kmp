package app.devper.pharm.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

data class ThemeController(
    val isDark: Boolean,
    val canToggle: Boolean,
    val toggle: () -> Unit,
) {
    companion object {
        val None = ThemeController(isDark = false, canToggle = false, toggle = {})
    }
}

val LocalThemeController = staticCompositionLocalOf { ThemeController.None }
