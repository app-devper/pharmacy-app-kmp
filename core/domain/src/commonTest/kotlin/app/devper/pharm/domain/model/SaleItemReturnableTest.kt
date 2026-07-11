package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money
import kotlin.test.Test
import kotlin.test.assertEquals

class SaleItemReturnableTest {

    private fun item(qty: Int, returned: Int = 0, lotBound: Int = 0, factor: Int = 0) = SaleItemSnapshot(
        id = "i1",
        drugId = "d1",
        drugName = "Drug",
        qty = qty,
        price = Money(5.0),
        originalPrice = Money(5.0),
        itemDiscount = Money.Zero,
        unit = "เม็ด",
        unitFactor = factor,
        priceTier = "",
        returnedQty = returned,
        lotBoundQty = lotBound,
    )

    @Test
    fun fully_lot_bound_item_is_returnable_up_to_remaining() {
        val snapshot = item(qty = 10, returned = 2, lotBound = 10)
        assertEquals(8, snapshot.returnableQty)
        assertEquals(0, snapshot.unreturnableQty)
    }

    @Test
    fun oversold_portion_reduces_returnable() {
        val snapshot = item(qty = 10, lotBound = 6)
        assertEquals(6, snapshot.returnableQty)
        assertEquals(4, snapshot.unreturnableQty)
    }

    @Test
    fun legacy_item_without_lot_splits_is_not_returnable() {
        val snapshot = item(qty = 5, lotBound = 0)
        assertEquals(0, snapshot.returnableQty)
        assertEquals(5, snapshot.unreturnableQty)
    }

    @Test
    fun returns_already_made_count_against_the_lot_bound_cap() {
        val snapshot = item(qty = 10, returned = 6, lotBound = 6)
        assertEquals(0, snapshot.returnableQty)
    }

    @Test
    fun alt_unit_returnable_converts_to_display_units() {
        val snapshot = item(qty = 20, lotBound = 10, factor = 10)
        assertEquals(1, snapshot.returnableDisplayQty)
    }
}
