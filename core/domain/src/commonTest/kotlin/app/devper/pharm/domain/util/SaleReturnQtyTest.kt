package app.devper.pharm.domain.util

import app.devper.pharm.domain.model.SaleItemSnapshot
import kotlin.test.Test
import kotlin.test.assertEquals

class SaleReturnQtyTest {

    private fun snap(qty: Int, returned: Int = 0, factor: Int = 1) = SaleItemSnapshot(
        id = "s1", drugId = "d1", drugName = "x", qty = qty,
        price = 1.0, originalPrice = 1.0, itemDiscount = 0.0,
        unit = "ชิ้น", unitFactor = factor, priceTier = "retail",
        returnedQty = returned,
    )

    @Test
    fun base_unit_passes_through() {
        assertEquals(3, SaleReturnQty.resolve(snap(qty = 10), displayQty = 3))
    }

    @Test
    fun alt_unit_multiplies_by_factor() {

        val item = snap(qty = 100, factor = 10)
        assertEquals(20, SaleReturnQty.resolve(item, displayQty = 2))
    }

    @Test
    fun coerced_to_zero_for_negative_input() {
        assertEquals(0, SaleReturnQty.resolve(snap(qty = 10), displayQty = -5))
    }

    @Test
    fun coerced_to_remaining_when_above_max() {

        assertEquals(3, SaleReturnQty.resolve(snap(qty = 10, returned = 7), displayQty = 99))
    }

    @Test
    fun factor_one_treated_as_base_unit() {

        assertEquals(2, SaleReturnQty.resolve(snap(qty = 5, factor = 1), displayQty = 2))
    }

    @Test
    fun factor_zero_or_negative_treated_as_base() {

        assertEquals(2, SaleReturnQty.resolve(snap(qty = 5, factor = 0), displayQty = 2))
    }
}
