package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.SaleItemSnapshot
import kotlin.test.Test
import kotlin.test.assertEquals

class SaleItemExtTest {

    private fun snap(qty: Int, returned: Int = 0, factor: Int = 1) = SaleItemSnapshot(
        id = "s1", drugId = "d1", drugName = "x", qty = qty,
        price = 1.0, originalPrice = 1.0, itemDiscount = 0.0,
        unit = "ชิ้น", unitFactor = factor, priceTier = "retail",
        returnedQty = returned,
    )

    @Test
    fun base_unit_passes_through() {
        assertEquals(3, snap(qty = 10).resolveReturnQty(displayQty = 3))
    }

    @Test
    fun alt_unit_multiplies_by_factor() {

        val item = snap(qty = 100, factor = 10)
        assertEquals(20, item.resolveReturnQty(displayQty = 2))
    }

    @Test
    fun coerced_to_zero_for_negative_input() {
        assertEquals(0, snap(qty = 10).resolveReturnQty(displayQty = -5))
    }

    @Test
    fun coerced_to_remaining_when_above_max() {

        assertEquals(3, snap(qty = 10, returned = 7).resolveReturnQty(displayQty = 99))
    }

    @Test
    fun factor_one_treated_as_base_unit() {

        assertEquals(2, snap(qty = 5, factor = 1).resolveReturnQty(displayQty = 2))
    }

    @Test
    fun factor_zero_or_negative_treated_as_base() {

        assertEquals(2, snap(qty = 5, factor = 0).resolveReturnQty(displayQty = 2))
    }
}
