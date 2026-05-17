package app.devper.pharm.ui.designsystem

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PharmStatusBadgeTest {

    @Test
    fun pending_is_amber() {
        assertEquals(PharmBadgeTone.Amber, PharmStatus.Pending.tone())
        assertEquals(PharmBadgeTone.Amber, PharmStatus.Draft.tone())
        assertEquals(PharmBadgeTone.Amber, PharmStatus.LowStock.tone())
    }

    @Test
    fun green_statuses() {
        assertEquals(PharmBadgeTone.Green, PharmStatus.Done.tone())
        assertEquals(PharmBadgeTone.Green, PharmStatus.Active.tone())
        assertEquals(PharmBadgeTone.Green, PharmStatus.Confirmed.tone())
        assertEquals(PharmBadgeTone.Green, PharmStatus.Normal.tone())
    }

    @Test
    fun red_statuses() {
        assertEquals(PharmBadgeTone.Red, PharmStatus.Voided.tone())
        assertEquals(PharmBadgeTone.Red, PharmStatus.Failed.tone())
        assertEquals(PharmBadgeTone.Red, PharmStatus.OutOfStock.tone())
        assertEquals(PharmBadgeTone.Red, PharmStatus.Backordered.tone())
    }

    @Test
    fun vip_is_purple() {
        assertEquals(PharmBadgeTone.Purple, PharmStatus.Vip.tone())
    }

    @Test
    fun every_status_has_default_label() {
        PharmStatus.values().forEach { status ->
            assertNotEquals("", status.defaultLabel(), "$status must have non-empty label")
        }
    }

    @Test
    fun inactive_is_gray() {
        assertEquals(PharmBadgeTone.Gray, PharmStatus.Inactive.tone())
    }
}
