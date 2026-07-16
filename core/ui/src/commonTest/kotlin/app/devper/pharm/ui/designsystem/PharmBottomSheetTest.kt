package app.devper.pharm.ui.designsystem

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PharmBottomSheetTest {
    @Test
    fun allowsEveryTransitionWhenDismissIsEnabled() {
        assertTrue(canChangeBottomSheetValue(dismissEnabled = true, isHiddenTarget = true))
        assertTrue(canChangeBottomSheetValue(dismissEnabled = true, isHiddenTarget = false))
    }

    @Test
    fun blocksOnlyHiddenTransitionWhenDismissIsDisabled() {
        assertFalse(canChangeBottomSheetValue(dismissEnabled = false, isHiddenTarget = true))
        assertTrue(canChangeBottomSheetValue(dismissEnabled = false, isHiddenTarget = false))
    }
}
