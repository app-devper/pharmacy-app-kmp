package app.devper.pharm.ui.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PharmFocusableTest {
    @Test
    fun keyboardFocusShowsRing() {
        val state = FocusVisibilityState()

        assertTrue(state.isVisible(isFocused = true))
    }

    @Test
    fun pointerFocusHidesRing() {
        val state = FocusVisibilityState().onPointerPress()

        assertFalse(state.isVisible(isFocused = true))
    }

    @Test
    fun unfocusedControlHidesRing() {
        val state = FocusVisibilityState()

        assertFalse(state.isVisible(isFocused = false))
    }

    @Test
    fun keyPressRestoresRingAfterPointerInteraction() {
        val state = FocusVisibilityState()
            .onPointerPress()
            .onKeyPress()

        assertTrue(state.isVisible(isFocused = true))
    }

    @Test
    fun focusLossResetsInteractionMode() {
        val state = FocusVisibilityState()
            .onPointerPress()
            .onFocusChanged(isFocused = false)

        assertEquals(FocusInteractionMode.Keyboard, state.interactionMode)
    }
}
