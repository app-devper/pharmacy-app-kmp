package app.devper.pharm.ui.components

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppShellLayoutTest {

    @Test
    fun compactDrawerUsesFullPhoneWidth() {
        assertEquals(320.dp, compactDrawerWidth(320.dp, 260.dp))
        assertEquals(360.dp, compactDrawerWidth(360.dp, 260.dp))
        assertEquals(390.dp, compactDrawerWidth(390.dp, 260.dp))
        assertEquals(430.dp, compactDrawerWidth(430.dp, 260.dp))
        assertEquals(599.dp, compactDrawerWidth(599.dp, 260.dp))
    }

    @Test
    fun mediumViewportUsesMobileShellWithBoundedDrawer() {
        assertTrue(usesCompactAppShell(WindowSize.fromWidth(599.dp)))
        assertTrue(usesCompactAppShell(WindowSize.fromWidth(600.dp)))
        assertTrue(usesCompactAppShell(WindowSize.fromWidth(839.dp)))
        assertFalse(usesCompactAppShell(WindowSize.fromWidth(840.dp)))
        assertEquals(260.dp, compactDrawerWidth(600.dp, 260.dp))
        assertEquals(260.dp, compactDrawerWidth(839.dp, 260.dp))
    }
}
