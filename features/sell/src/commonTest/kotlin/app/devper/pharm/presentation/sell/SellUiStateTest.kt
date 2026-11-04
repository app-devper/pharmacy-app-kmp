package app.devper.pharm.presentation.sell

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Drug
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    fun grossSubtotal_countsAnAltUnitLineOnce_notOncePerBaseUnit() {
        val box = AltUnit(name = "กล่อง", factor = 10, sellPrice = Money(100.0))
        val state = SellUiState(cart = listOf(CartLine(drug = drug(), qty = 20, selectedUnit = box)))

        assertEquals(Money(200.0), state.grossSubtotal)
        assertEquals(Money.Zero, state.itemDiscountTotal)
    }

    @Test
    fun itemDiscountTotal_isTheDiscountGivenOnLines() {
        val line = CartLine(drug = drug(sellPrice = Money(10.0)), qty = 3, discount = Money(1.0))
        val state = SellUiState(cart = listOf(line))

        assertEquals(Money(30.0), state.grossSubtotal)
        assertEquals(Money(3.0), state.itemDiscountTotal)
    }

    @Test
    fun itemDiscountTotal_isExactlyZero_forAnUndiscountedPack() {
        val pack = AltUnit(name = "แพ็ค", factor = 6, sellPrice = Money(35.0))
        val state = SellUiState(cart = listOf(CartLine(drug = drug(), qty = 18, selectedUnit = pack)))

        assertEquals(Money.Zero, state.itemDiscountTotal)
        assertTrue(state.itemDiscountTotal.isZero)
    }
}
