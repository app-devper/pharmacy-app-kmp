package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import kotlin.test.Test
import kotlin.test.assertEquals

class CartLineTest {

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
    fun multiUnitLine_grossValue_usesDisplayQty_notBaseQty() {
        val box = AltUnit(name = "กล่อง", factor = 10, sellPrice = Money(100.0))
        val line = CartLine(drug = drug(sellPrice = Money(10.0)), qty = 20, selectedUnit = box)

        assertEquals(2, line.displayQty)
        assertEquals(Money(10.0), line.basePrice)
        assertEquals(Money(100.0), line.unitPrice)
        assertEquals(Money(200.0), line.unitPrice * line.displayQty)
        assertEquals(line.basePrice * line.qty, line.unitPrice * line.displayQty)
        assertEquals(Money(200.0), line.lineTotal)
    }

    @Test
    fun singleUnitLine_grossValue_matchesLineTotal() {
        val line = CartLine(drug = drug(sellPrice = Money(5.0)), qty = 3)

        assertEquals(3, line.displayQty)
        assertEquals(Money(15.0), line.unitPrice * line.displayQty)
        assertEquals(Money(15.0), line.lineTotal)
    }

    @Test
    fun altUnitLine_pricesEachBaseUnitToTheSatang_asPharmacyApiRecordsIt() {
        val pack = AltUnit(name = "แพ็ค", factor = 3, sellPrice = Money(100.0))
        val line = CartLine(drug = drug(), qty = 3, selectedUnit = pack)

        assertEquals(Money(33.33), line.basePrice)
        assertEquals("99.99", line.lineTotal.format())
    }

    @Test
    fun tierPriceOfZero_fallsBackToRetail_asPharmacyApiDoes() {
        val zeroWholesale = drug(sellPrice = Money(10.0)).copy(
            prices = mapOf("wholesale" to Money(0.0), "retail" to Money(12.0)),
        )
        val line = CartLine(drug = zeroWholesale, qty = 1, tier = "wholesale")

        assertEquals(Money(12.0), line.basePrice)
    }
}
