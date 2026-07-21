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

    @Test
    fun enabledHoveredIconUsesHoverScale() {
        assertEquals(
            PHARM_ICON_HOVER_SCALE,
            iconHoverTargetScale(enabled = true, hovered = true, reducedMotion = false),
        )
    }

    @Test
    fun iconKeepsRestScaleOutsideHover() {
        assertEquals(1f, iconHoverTargetScale(enabled = true, hovered = false, reducedMotion = false))
        assertEquals(1f, iconHoverTargetScale(enabled = false, hovered = true, reducedMotion = false))
    }

    @Test
    fun reducedMotionDisablesIconHoverScale() {
        assertEquals(1f, iconHoverTargetScale(enabled = true, hovered = true, reducedMotion = true))
    }
}
