package app.devper.pharm.ui.designsystem

import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmDimens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PharmTopbarLayoutTest {

    @Test
    fun compactTopbarUsesChatGptMobileDimensions() {
        val dimens = PharmDimens()

        assertEquals(52.dp, dimens.compactTopbarHeight)
        assertEquals(8.dp, dimens.compactTopbarStartPadding)
        assertEquals(16.dp, dimens.compactTopbarEndPadding)
        assertEquals(8.dp, dimens.compactTopbarItemSpacing)
        assertEquals(14.dp, dimens.compactTopbarActionPaddingX)
        assertEquals(132.dp, dimens.compactTopbarActionMaxWidth)
        assertEquals(36.dp, dimens.compactControlHeight)
        assertEquals(36.dp, dimens.minimumTouchTarget)
    }

    @Test
    fun topbarUsesTheSameBreakpointAsCompactShell() {
        assertTrue(usesCompactTopbar(320.dp))
        assertTrue(usesCompactTopbar(519.dp))
        assertTrue(usesCompactTopbar(520.dp))
        assertTrue(usesCompactTopbar(599.dp))
        assertTrue(usesCompactTopbar(600.dp))
        assertTrue(usesCompactTopbar(839.dp))
        assertFalse(usesCompactTopbar(840.dp))
    }
}
