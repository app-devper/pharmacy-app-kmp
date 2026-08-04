package app.devper.pharm.ui.designsystem

import androidx.compose.ui.graphics.Color
import app.devper.pharm.ui.theme.DarkPharmTokens
import app.devper.pharm.ui.theme.LightPharmTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ToggleSurfaceTest {

    @Test
    fun selectedToggleIsDistinctFromHoveredToggle() {
        listOf(LightPharmTokens.colors, DarkPharmTokens.colors).forEach { colors ->
            val selected = toggleSurface(active = true, hovered = false, colors = colors)
            val hovered = toggleSurface(active = false, hovered = true, colors = colors)

            assertNotEquals(hovered, selected)
        }
    }

    @Test
    fun selectedToggleKeepsItsSurfaceWhileHovered() {
        val colors = LightPharmTokens.colors

        assertEquals(
            toggleSurface(active = true, hovered = false, colors = colors),
            toggleSurface(active = true, hovered = true, colors = colors),
        )
    }

    @Test
    fun restingToggleIsTransparent() {
        assertEquals(
            Color.Transparent,
            toggleSurface(active = false, hovered = false, colors = LightPharmTokens.colors),
        )
    }

    @Test
    fun onlySelectedToggleDrawsABorder() {
        val colors = LightPharmTokens.colors

        assertEquals(colors.border, toggleBorder(active = true, colors = colors))
        assertEquals(Color.Transparent, toggleBorder(active = false, colors = colors))
    }
}
