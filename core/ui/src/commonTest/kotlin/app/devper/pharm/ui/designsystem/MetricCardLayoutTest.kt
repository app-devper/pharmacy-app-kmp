package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.components.WindowSize
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MetricCardLayoutTest {

    @Test
    fun everyWindowBelowExpandedUsesStats() {
        assertTrue(usesMetricStats(WindowSize.Compact))
        assertTrue(usesMetricStats(WindowSize.Medium))
    }

    @Test
    fun expandedKeepsTheCardGrid() {
        assertFalse(usesMetricStats(WindowSize.Expanded))
    }

    @Test
    fun statsFollowTheSameBoundaryAsTheCompactShell() {
        WindowSize.entries.forEach { size ->
            assertTrue(usesMetricStats(size) == app.devper.pharm.ui.components.usesCompactAppShell(size))
        }
    }
}
