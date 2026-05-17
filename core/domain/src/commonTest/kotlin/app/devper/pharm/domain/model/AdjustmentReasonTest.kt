package app.devper.pharm.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class AdjustmentReasonTest {

    @Test
    fun fromWire_returns_matching_enum_for_known_values() {
        assertEquals(AdjustmentReason.Recount, AdjustmentReason.fromWire("นับสต็อก"))
        assertEquals(AdjustmentReason.Damaged, AdjustmentReason.fromWire("ยาเสียหาย"))
        assertEquals(AdjustmentReason.Expired, AdjustmentReason.fromWire("ยาหมดอายุ"))
        assertEquals(AdjustmentReason.Lost, AdjustmentReason.fromWire("สูญหาย"))
        assertEquals(AdjustmentReason.Other, AdjustmentReason.fromWire("อื่นๆ"))
    }

    @Test
    fun fromWire_returns_Other_for_unknown_value() {
        assertEquals(AdjustmentReason.Other, AdjustmentReason.fromWire("ยาเสียกลิ่น"))
        assertEquals(AdjustmentReason.Other, AdjustmentReason.fromWire(""))
    }

    @Test
    fun fromWire_returns_Other_for_null() {
        assertEquals(AdjustmentReason.Other, AdjustmentReason.fromWire(null))
    }

    @Test
    fun pickerOrder_starts_with_most_common() {
        assertEquals(AdjustmentReason.Recount, AdjustmentReason.pickerOrder.first())
        assertEquals(AdjustmentReason.Other, AdjustmentReason.pickerOrder.last())

        assertEquals(AdjustmentReason.entries.size, AdjustmentReason.pickerOrder.toSet().size)
    }
}
