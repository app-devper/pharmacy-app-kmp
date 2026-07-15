package app.devper.pharm.ui.common

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ResponsiveSnackbarTest {
    @Test
    fun compactToastStacksActionBelowMessage() {
        assertTrue(shouldStackToast(320.dp))
        assertTrue(shouldStackToast(359.dp))
        assertFalse(shouldStackToast(360.dp))
    }

    @Test
    fun compactSnackbarClearsBottomNavigation() {
        assertEquals(136.dp, snackbarBottomPadding(320.dp))
        assertEquals(136.dp, snackbarBottomPadding(599.dp))
        assertEquals(24.dp, snackbarBottomPadding(600.dp))
    }

    @Test
    fun narrowSnackbarUsesSmallerHorizontalGutter() {
        assertEquals(12.dp, snackbarHorizontalPadding(320.dp))
        assertEquals(12.dp, snackbarHorizontalPadding(359.dp))
        assertEquals(24.dp, snackbarHorizontalPadding(360.dp))
    }
}
