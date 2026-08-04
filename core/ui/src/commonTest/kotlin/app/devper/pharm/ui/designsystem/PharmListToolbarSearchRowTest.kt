package app.devper.pharm.ui.designsystem

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PharmListToolbarSearchRowTest {

    @Test
    fun onPhonesTheSearchFieldSharesItsRowWithTheActions() {
        assertTrue(
            searchSharesRowWithActions(compact = true, hasSearch = true, hasInlineActions = true),
        )
    }

    @Test
    fun aSearchFieldWithNoActionsKeepsTheRowToItself() {
        assertFalse(
            searchSharesRowWithActions(compact = true, hasSearch = true, hasInlineActions = false),
        )
    }

    @Test
    fun actionsWithNoSearchFieldStayWithTheFilters() {
        assertFalse(
            searchSharesRowWithActions(compact = true, hasSearch = false, hasInlineActions = true),
        )
    }

    @Test
    fun wideLayoutsAreUntouched() {
        assertFalse(
            searchSharesRowWithActions(compact = false, hasSearch = true, hasInlineActions = true),
        )
    }

    @Test
    fun theFilterRowStopsCombiningOnceTheSearchRowHasTheActions() {
        val searchTook = searchSharesRowWithActions(compact = true, hasSearch = true, hasInlineActions = true)

        assertFalse(
            combinesCompactToolbarControls(
                compact = true,
                hasFilters = true,
                hasInlineActions = !searchTook,
                allowSharedRow = true,
            ),
        )
    }
}
