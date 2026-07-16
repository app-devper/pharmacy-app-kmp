package app.devper.pharm.ui.designsystem

import kotlin.test.Test
import kotlin.test.assertEquals

class PharmMotionTest {
    @Test
    fun reducedMotionRemovesAnimationDuration() {
        assertEquals(0, motionDurationMillis(reducedMotion = true, durationMillis = PharmMotion.Medium))
    }

    @Test
    fun standardMotionKeepsAnimationDuration() {
        assertEquals(PharmMotion.Fast, motionDurationMillis(reducedMotion = false, durationMillis = PharmMotion.Fast))
    }
}
