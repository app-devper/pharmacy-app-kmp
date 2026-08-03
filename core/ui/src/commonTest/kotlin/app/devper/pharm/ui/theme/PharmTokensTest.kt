package app.devper.pharm.ui.theme

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PharmTokensTest {

    @Test
    fun darkSurfacesStackFromSidebarUpToRaised() {
        val c = DarkPharmColors

        assertEquals(SidebarDark, c.sidebarBg)
        assertEquals(PaperDark, c.bgPage)
        assertEquals(SurfaceDark, c.surface)
        assertEquals(SurfaceRaisedDark, c.surfaceRaised)
        assertTrue(c.sidebarBg.luminanceRank() < c.bgPage.luminanceRank())
        assertTrue(c.bgPage.luminanceRank() < c.surface.luminanceRank())
        assertTrue(c.surface.luminanceRank() < c.surfaceRaised.luminanceRank())
    }

    @Test
    fun darkSidebarIsDistinctFromPage() {
        assertNotEquals(DarkPharmColors.bgPage, DarkPharmColors.sidebarBg)
    }

    @Test
    fun darkHoverLiftsAwayFromItsBackground() {
        val c = DarkPharmColors

        assertTrue(c.hoverSurface.luminanceRank() > c.bgPage.luminanceRank())
        assertTrue(c.hoverSurfaceRaised.luminanceRank() > c.surfaceRaised.luminanceRank())
    }

    @Test
    fun lightHoverSettlesBelowItsBackground() {
        val c = LightPharmColors

        assertTrue(c.hoverSurface.luminanceRank() < c.bgPage.luminanceRank())
        assertTrue(c.selectedSurface.luminanceRank() < c.hoverSurface.luminanceRank())
    }

    @Test
    fun dangerButtonKeepsReadableForegroundInBothThemes() {
        assertEquals(White, LightPharmColors.dangerActionFg)
        assertEquals(White, DarkPharmColors.dangerActionFg)
        assertEquals(LightPharmColors.dangerActionBg, DarkPharmColors.dangerActionBg)
    }

    @Test
    fun touchInputRaisesTheMinimumTargetToFortyFour() {
        assertEquals(36.dp, LightPharmTokens.dimens.minimumTouchTarget)
        assertEquals(44.dp, LightPharmTokens.forTouchInput().dimens.minimumTouchTarget)
        assertEquals(44.dp, DarkPharmTokens.forTouchInput().dimens.minimumTouchTarget)
    }

    @Test
    fun touchInputLeavesEverythingElseAlone() {
        val base = LightPharmTokens

        assertEquals(base.colors, base.forTouchInput().colors)
        assertEquals(base.dimens.sidebarRowHeight, base.forTouchInput().dimens.sidebarRowHeight)
        assertEquals(base.dimens.compactControlHeight, base.forTouchInput().dimens.compactControlHeight)
    }

    @Test
    fun smallButtonsAreTighterThanMediumButtons() {
        assertTrue(LightPharmTokens.dimens.buttonSmPaddingX < 16.dp)
    }
}

private fun androidx.compose.ui.graphics.Color.luminanceRank(): Float = red + green + blue
