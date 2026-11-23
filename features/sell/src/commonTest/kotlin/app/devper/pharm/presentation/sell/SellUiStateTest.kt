package app.devper.pharm.presentation.sell

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Drug
import kotlin.test.Test
import kotlin.test.assertEquals

class SellUiStateTest {

    private fun drug(sellPrice: Money = Money(10.0)) = Drug(
        id = "d1",
        name = "Amoxicillin",
        genericName = null,
        type = null,
        strength = null,
        barcode = null,
        sellPrice = sellPrice,
        costPrice = Money(0.0),
        stock = Quantity(100),
        minStock = Quantity(0),
        unit = "เม็ด",
        regNo = null,
    )

    @Test
    fun totals_areTheBillsTotals() {
        val line = CartLine(drug = drug(), qty = 3, discount = Money(1.0))
        val state = SellUiState(
            cart = listOf(line),
            cartDiscount = CartDiscount.Percent(7.0),
            received = "100",
        )

        assertEquals(state.bill.total, state.total)
        assertEquals(state.bill.itemDiscountTotal, state.itemDiscountTotal)
        assertEquals(state.bill.change(Money(state.receivedNum)), state.change)
    }
}
