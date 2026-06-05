package app.devper.pharm.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class CartLineTest {

    private fun drug(sellPrice: Double = 10.0) = Drug(
        id = "d1",
        name = "Amoxicillin",
        genericName = null,
        type = null,
        strength = null,
        barcode = null,
        sellPrice = sellPrice,
        costPrice = 0.0,
        stock = 100,
        minStock = 0,
        unit = "เม็ด",
        regNo = null,
    )

    @Test
    fun multiUnitLine_grossValue_usesDisplayQty_notBaseQty() {
        val box = AltUnit(name = "กล่อง", factor = 10, sellPrice = 100.0)
        val line = CartLine(drug = drug(sellPrice = 10.0), qty = 20, selectedUnit = box)

        assertEquals(2, line.displayQty)
        assertEquals(10.0, line.basePrice)
        assertEquals(100.0, line.unitPrice)
        assertEquals(200.0, line.unitPrice * line.displayQty)
        assertEquals(line.basePrice * line.qty, line.unitPrice * line.displayQty)
        assertEquals(200.0, line.lineTotal)
    }

    @Test
    fun singleUnitLine_grossValue_matchesLineTotal() {
        val line = CartLine(drug = drug(sellPrice = 5.0), qty = 3)

        assertEquals(3, line.displayQty)
        assertEquals(15.0, line.unitPrice * line.displayQty)
        assertEquals(15.0, line.lineTotal)
    }
}
