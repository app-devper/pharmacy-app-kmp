package app.devper.pharm.ui.designsystem

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PharmDensityTest {

    @Test
    fun compactRowsAreShorterThanComfortableOnes() {
        assertTrue(PharmDensity.Compact.rowHeight < PharmDensity.Comfortable.rowHeight)
        assertTrue(PharmDensity.Compact.headerHeight < PharmDensity.Comfortable.headerHeight)
    }

    @Test
    fun anUnspecifiedRowHeightDefersToTheDensity() {
        PharmDensity.entries.forEach { density ->
            assertEquals(density.rowHeight, resolvedRowHeight(Dp.Unspecified, density))
            assertEquals(density.headerHeight, resolvedHeaderHeight(Dp.Unspecified, density))
        }
    }

    @Test
    fun anExplicitRowHeightOverridesTheDensity() {
        assertEquals(76.dp, resolvedRowHeight(76.dp, PharmDensity.Compact))
        assertEquals(76.dp, resolvedRowHeight(76.dp, PharmDensity.Comfortable))
    }
}
