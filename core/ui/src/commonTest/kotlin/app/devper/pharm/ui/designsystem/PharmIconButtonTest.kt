package app.devper.pharm.ui.designsystem

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PharmIconButtonTest {
    @Test
    fun enabledHoverShowsContainer() {
        assertTrue(
            showsIconButtonContainer(
                enabled = true,
                hovered = true,
                selected = false,
            ),
        )
    }

    @Test
    fun disabledHoverDoesNotShowContainer() {
        assertFalse(
            showsIconButtonContainer(
                enabled = false,
                hovered = true,
                selected = false,
            ),
        )
    }

    @Test
    fun selectedAlwaysShowsContainer() {
        assertTrue(
            showsIconButtonContainer(
                enabled = false,
                hovered = false,
                selected = true,
            ),
        )
    }
}
