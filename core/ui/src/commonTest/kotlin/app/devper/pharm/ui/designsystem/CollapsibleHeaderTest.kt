package app.devper.pharm.ui.designsystem

import kotlin.test.Test
import kotlin.test.assertEquals

class CollapsibleHeaderTest {

    @Test
    fun scrollingUpCollapsesTheHeaderNoFurtherThanItsHeight() {
        assertEquals(-40f, collapsedHeaderOffset(current = 0f, delta = -40f, maxCollapse = 96f))
        assertEquals(-96f, collapsedHeaderOffset(current = -80f, delta = -40f, maxCollapse = 96f))
    }

    @Test
    fun scrollingDownExpandsTheHeaderNoFurtherThanFullyOpen() {
        assertEquals(-20f, collapsedHeaderOffset(current = -60f, delta = 40f, maxCollapse = 96f))
        assertEquals(0f, collapsedHeaderOffset(current = -20f, delta = 40f, maxCollapse = 96f))
    }

    @Test
    fun consumptionIsOnlyTheDistanceTheHeaderActuallyMoved() {
        assertEquals(-16f, headerScrollConsumption(current = -80f, delta = -40f, maxCollapse = 96f))
        assertEquals(0f, headerScrollConsumption(current = -96f, delta = -40f, maxCollapse = 96f))
        assertEquals(0f, headerScrollConsumption(current = 0f, delta = 40f, maxCollapse = 96f))
    }

    @Test
    fun aHeaderWithNoHeightNeverConsumesScroll() {
        assertEquals(
            expected = 0f,
            actual = headerScrollConsumption(current = 0f, delta = -40f, maxCollapse = 0f),
            absoluteTolerance = 0f,
        )
    }

    @Test
    fun stateConsumesUntilCollapsedThenLetsTheListScroll() {
        val state = CollapsibleHeaderState()
        state.onHeaderMeasured(80)

        assertEquals(-50f, state.consume(-50f))
        assertEquals(-30f, state.consume(-40f))
        assertEquals(0f, state.consume(-40f))
        assertEquals(-80f, state.offsetPx)
    }

    @Test
    fun shrinkingHeaderPullsAnOverCollapsedOffsetBackIntoRange() {
        val state = CollapsibleHeaderState()
        state.onHeaderMeasured(120)
        state.consume(-120f)

        state.onHeaderMeasured(40)

        assertEquals(-40f, state.offsetPx)
    }
}
