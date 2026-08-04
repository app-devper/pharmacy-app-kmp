package app.devper.pharm.ui.designsystem

import androidx.compose.ui.unit.dp
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

    @Test
    fun phoneModalUsesFullScreenPresentation() {
        assertEquals(PharmModalPresentation.FullScreen, modalPresentation(320.dp))
        assertEquals(PharmModalPresentation.FullScreen, modalPresentation(360.dp))
        assertEquals(PharmModalPresentation.FullScreen, modalPresentation(599.dp))
    }

    @Test
    fun tabletAndDesktopModalFloatAboveThePage() {
        assertEquals(PharmModalPresentation.Floating, modalPresentation(600.dp))
        assertEquals(PharmModalPresentation.Floating, modalPresentation(840.dp))
    }

    @Test
    fun modalSizesHaveStableResponsiveCaps() {
        assertEquals(384.dp, modalWidth(PharmModalSize.Sm))
        assertEquals(448.dp, modalWidth(PharmModalSize.Md))
        assertEquals(672.dp, modalWidth(PharmModalSize.Lg))
        assertEquals(896.dp, modalWidth(PharmModalSize.Xl))
    }
}
