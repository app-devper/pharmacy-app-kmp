package app.devper.pharm.ui.components

import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.pageHorizontalGutter
import app.devper.pharm.ui.designsystem.usesMetricStats
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ResponsiveBreakpointsTest {

    @Test
    fun targetQaWidthsResolveToExpectedLayouts() {
        assertEquals(WindowSize.Compact, WindowSize.fromWidth(320.dp))
        assertEquals(WindowSize.Compact, WindowSize.fromWidth(360.dp))
        assertEquals(WindowSize.Medium, WindowSize.fromWidth(600.dp))
        assertEquals(WindowSize.Expanded, WindowSize.fromWidth(840.dp))
    }

    @Test
    fun phonesGetATighterPageGutter() {
        assertEquals(16.dp, pageHorizontalGutter(WindowSize.fromWidth(320.dp)))
        assertEquals(16.dp, pageHorizontalGutter(WindowSize.fromWidth(360.dp)))
        assertEquals(24.dp, pageHorizontalGutter(WindowSize.fromWidth(600.dp)))
        assertEquals(24.dp, pageHorizontalGutter(WindowSize.fromWidth(840.dp)))
    }

    @Test
    fun theShellTierCoversEverythingThatIsNotExpanded() {
        assertTrue(WindowSize.Compact.isCompactShell)
        assertTrue(WindowSize.Medium.isCompactShell)
        assertFalse(WindowSize.Expanded.isCompactShell)
    }

    @Test
    fun theContentTierIsPhonesOnly() {
        assertTrue(WindowSize.Compact.isCompactContent)
        assertFalse(WindowSize.Medium.isCompactContent)
        assertFalse(WindowSize.Expanded.isCompactContent)
    }

    @Test
    fun chromeDecisionsFollowTheShellTier() {
        WindowSize.entries.forEach { size ->
            assertEquals(size.isCompactShell, usesCompactAppShell(size))
            assertEquals(size.isCompactShell, usesMetricStats(size))
        }
    }

    @Test
    fun theGutterIsTheContentTierNotTheShellTier() {
        assertEquals(24.dp, pageHorizontalGutter(WindowSize.Medium))
    }
}
