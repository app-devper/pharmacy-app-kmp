package app.devper.pharm.ui.designsystem

import kotlin.test.Test
import kotlin.test.assertEquals

class PharmModalTest {
    @Test
    fun customRequesterHasPriorityOverCloseControl() {
        assertEquals(
            expected = PharmModalInitialFocusTarget.Custom,
            actual = modalInitialFocusTarget(
                hasCustomRequester = true,
                hasDismissibleClose = true,
            ),
        )
    }

    @Test
    fun dismissibleCloseIsTheDefaultFocusTarget() {
        assertEquals(
            expected = PharmModalInitialFocusTarget.Close,
            actual = modalInitialFocusTarget(
                hasCustomRequester = false,
                hasDismissibleClose = true,
            ),
        )
    }

    @Test
    fun modalWithoutFocusableEntryTargetDoesNotRequestFocus() {
        assertEquals(
            expected = PharmModalInitialFocusTarget.None,
            actual = modalInitialFocusTarget(
                hasCustomRequester = false,
                hasDismissibleClose = false,
            ),
        )
    }
}
