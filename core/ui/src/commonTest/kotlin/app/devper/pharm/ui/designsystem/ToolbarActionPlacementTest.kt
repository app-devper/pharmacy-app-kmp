package app.devper.pharm.ui.designsystem

import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.WindowSize
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ToolbarActionPlacementTest {
    @Test
    fun list_pages_put_primary_in_topbar_and_secondary_in_content_until_expanded() {
        listOf(320, 599, 600, 839).forEach { width ->
            val placement = toolbarActionPlacement(WindowSize.fromWidth(width.dp), hasBack = false)
            assertTrue(placement.primaryInTopbar)
            assertFalse(placement.secondaryInTopbar)
        }
        val expanded = toolbarActionPlacement(WindowSize.fromWidth(840.dp), hasBack = false)
        assertFalse(expanded.primaryInTopbar)
        assertFalse(expanded.secondaryInTopbar)
    }

    @Test
    fun subpages_move_all_actions_with_the_header() {
        val compact = toolbarActionPlacement(WindowSize.Compact, hasBack = true)
        assertTrue(compact.primaryInTopbar)
        assertTrue(compact.secondaryInTopbar)
        val expanded = toolbarActionPlacement(WindowSize.Expanded, hasBack = true)
        assertFalse(expanded.primaryInTopbar)
        assertFalse(expanded.secondaryInTopbar)
    }

    @Test
    fun wide_subpage_controls_can_remain_inline() {
        val placement = toolbarActionPlacement(WindowSize.Medium, hasBack = true, compactHeaderActions = false)
        assertFalse(placement.primaryInTopbar)
        assertFalse(placement.secondaryInTopbar)
    }
}
