package app.devper.pharm.ui.i18n

import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.domain.model.MovementType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DomainLabelsTest {

    @Test
    fun every_ky_form_type_has_a_nonblank_distinct_nav_title() {
        val th = KyFormType.entries.map { it.navTitle(PharmStringsTh) }
        th.forEach { assertTrue(it.isNotBlank()) }
        assertEquals(th.size, th.toSet().size)
        KyFormType.entries.forEach {
            assertNotEquals(it.navTitle(PharmStringsTh), it.navTitle(PharmStringsEn))
        }
    }

    @Test
    fun every_movement_type_localizes_nonblank_in_both_languages() {
        MovementType.entries.forEach {
            assertTrue(it.localizedLabel(PharmStringsTh).isNotBlank(), "$it Th")
            assertTrue(it.localizedLabel(PharmStringsEn).isNotBlank(), "$it En")
        }
    }

    @Test
    fun adjustment_reason_label_is_separate_from_wire() {
        AdjustmentReason.entries.forEach {
            assertTrue(it.label(PharmStringsTh).isNotBlank())
            assertTrue(it.label(PharmStringsEn).isNotBlank())
        }
        assertEquals("นับสต็อก", AdjustmentReason.Recount.wire)
        assertNotEquals(AdjustmentReason.Recount.wire, AdjustmentReason.Recount.label(PharmStringsEn))
    }
}
