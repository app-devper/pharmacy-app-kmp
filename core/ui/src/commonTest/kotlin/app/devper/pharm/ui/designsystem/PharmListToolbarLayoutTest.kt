package app.devper.pharm.ui.designsystem

import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.WindowSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PharmListToolbarLayoutTest {

    @Test
    fun compactFiltersAndInlineActionsShareOneControlRow() {
        assertTrue(
            combinesCompactToolbarControls(
                compact = true,
                hasFilters = true,
                hasInlineActions = true,
            ),
        )
        assertFalse(
            combinesCompactToolbarControls(
                compact = false,
                hasFilters = true,
                hasInlineActions = true,
            ),
        )
    }

    @Test
    fun compactToolbarMatchesMobileHeaderSpacing() {
        assertEquals(
            12.dp,
            listToolbarTopPadding(
                windowSize = WindowSize.Compact,
                expandedPadding = 48.dp,
            ),
        )
        assertEquals(
            12.dp,
            listToolbarTopPadding(
                windowSize = WindowSize.Medium,
                expandedPadding = 48.dp,
            ),
        )
        assertTrue(usesCompactListToolbar(WindowSize.Medium, 839.dp))
        assertFalse(usesCompactListToolbar(WindowSize.Expanded, 840.dp))
    }

    @Test
    fun compactSearchAndFiltersUseSeparatedSections() {
        assertEquals(
            32.dp,
            listToolbarSectionSpacing(
                compact = true,
                hasSearch = true,
                hasFilters = true,
            ),
        )
        assertEquals(
            16.dp,
            listToolbarSectionSpacing(
                compact = true,
                hasSearch = true,
                hasFilters = false,
            ),
        )
        assertEquals(
            16.dp,
            listToolbarSectionSpacing(
                compact = false,
                hasSearch = true,
                hasFilters = true,
            ),
        )
    }

    @Test
    fun topbarOnlyActionDoesNotReserveAnEmptyToolbarRow() {
        assertFalse(
            hasCompactToolbarContent(
                showTitle = false,
                hasBack = false,
                hasSearch = false,
                hasFilters = false,
                hasBadge = false,
                hasInlineActions = false,
            ),
        )
        assertTrue(
            hasCompactToolbarContent(
                showTitle = false,
                hasBack = false,
                hasSearch = true,
                hasFilters = false,
                hasBadge = false,
                hasInlineActions = false,
            ),
        )
    }

    @Test
    fun onlyCompactShellMovesListActionsIntoShellTopbar() {
        assertTrue(
            movesListToolbarActionsToTopbar(
                windowSize = WindowSize.fromWidth(320.dp),
                hasBack = false,
                compactTopbarActions = true,
            ),
        )
        assertTrue(
            movesListToolbarActionsToTopbar(
                windowSize = WindowSize.fromWidth(600.dp),
                hasBack = false,
                compactTopbarActions = true,
            ),
        )
        assertFalse(
            movesListToolbarActionsToTopbar(
                windowSize = WindowSize.fromWidth(840.dp),
                hasBack = false,
                compactTopbarActions = true,
            ),
        )
    }

    @Test
    fun compactSubpageMovesBackAndTitleIntoShellTopbar() {
        assertTrue(movesSubpageHeaderToTopbar(WindowSize.fromWidth(320.dp), hasBack = true))
        assertTrue(movesSubpageHeaderToTopbar(WindowSize.fromWidth(599.dp), hasBack = true))
        assertTrue(movesSubpageHeaderToTopbar(WindowSize.fromWidth(600.dp), hasBack = true))
        assertTrue(movesSubpageHeaderToTopbar(WindowSize.fromWidth(839.dp), hasBack = true))
        assertFalse(movesSubpageHeaderToTopbar(WindowSize.fromWidth(840.dp), hasBack = true))
    }
}
