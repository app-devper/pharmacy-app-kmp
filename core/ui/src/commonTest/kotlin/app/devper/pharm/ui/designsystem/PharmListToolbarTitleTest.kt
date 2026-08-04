package app.devper.pharm.ui.designsystem

import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.PharmBreakpoint
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PharmListToolbarTitleTest {

    @Test
    fun aRoomyToolbarKeepsItsTitle() {
        assertFalse(
            hidesToolbarTitleForSearch(compact = false, hasSearch = true, availableWidth = 768.dp),
        )
    }

    @Test
    fun theTitleStepsAsideOnceTheSearchFieldCrowdsIt() {
        assertTrue(
            hidesToolbarTitleForSearch(compact = false, hasSearch = true, availableWidth = 600.dp),
        )
        assertTrue(
            hidesToolbarTitleForSearch(
                compact = false,
                hasSearch = true,
                availableWidth = PharmBreakpoint.FormThreeCol - 1.dp,
            ),
        )
    }

    @Test
    fun aToolbarWithNoSearchFieldKeepsItsTitleAtAnyWidth() {
        assertFalse(
            hidesToolbarTitleForSearch(compact = false, hasSearch = false, availableWidth = 600.dp),
        )
    }

    @Test
    fun phonesAreLeftAlone() {
        assertFalse(
            hidesToolbarTitleForSearch(compact = true, hasSearch = true, availableWidth = 390.dp),
        )
    }
}
