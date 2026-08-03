package app.devper.pharm.ui.designsystem

import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmDimens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PharmSidebarLayoutTest {

    @Test
    fun sidebarUsesChatGptWebHeights() {
        val dimens = PharmDimens()

        assertEquals(36.dp, dimens.navRowHeight)
        assertEquals(56.dp, dimens.sidebarHeaderHeight)
        assertEquals(36.dp, dimens.minimumTouchTarget)
        assertEquals(36.dp, dimens.actionMenuRowHeight)
        assertEquals(56.dp, dimens.accountSummaryHeight)
    }

    @Test
    fun collapsedSidebarUsesChatGptWebRailWidth() {
        assertEquals(52.dp, sidebarTargetWidth(collapsed = true, expandedWidth = 260.dp))
        assertEquals(260.dp, sidebarTargetWidth(collapsed = false, expandedWidth = 260.dp))
    }

    @Test
    fun accountMenuFollowsSidebarWidth() {
        assertEquals(244.dp, sidebarAccountMenuWidth(260.dp))
        assertEquals(304.dp, sidebarAccountMenuWidth(320.dp))
        assertEquals(344.dp, sidebarAccountMenuWidth(360.dp))
    }

    @Test
    fun sellMenuIsPinnedAboveScrollableNavigation() {
        val sell = DefaultPharmNav.first { it.id == "sell" }
        val stock = DefaultPharmNav.first { it.id == "stock" }

        assertTrue(sell.pinned)
        assertFalse(stock.pinned)
    }
}
