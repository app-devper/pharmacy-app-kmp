package app.devper.pharm.ui.print

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.model.priceBill
import kotlin.test.Test
import kotlin.test.assertEquals

class ReceiptBuilderTest {

    private fun drug() = Drug(
        id = "d1",
        name = "Amoxicillin",
        genericName = null,
        type = null,
        strength = null,
        barcode = null,
        sellPrice = Money(10.0),
        costPrice = Money(0.0),
        stock = Quantity(1000),
        minStock = Quantity(0),
        unit = "เม็ด",
        regNo = null,
    )

    private fun sale() = Sale(
        id = "s1",
        billNo = "B1",
        total = Money(104.94),
        change = Money(0.0),
        discount = Money.Zero,
        stockUpdates = emptyList(),
    )

    private fun template(lines: List<CartLine>) = buildReceiptTemplate(
        sale = sale(),
        bill = priceBill(lines, CartDiscount.None),
        lines = lines,
        customer = null,
        settings = Settings(),
        received = 105.0,
        soldAtFormatted = "now",
    )

    @Test
    fun undiscountedPack_printsNoItemDiscount() {
        val pack = AltUnit(name = "แผง", factor = 6, sellPrice = Money(35.0))

        val receipt = template(listOf(CartLine(drug(), qty = 18, selectedUnit = pack)))

        assertEquals(0.0, receipt.itemDiscountTotal)
        assertEquals(104.94, receipt.subtotal)
    }

    @Test
    fun lineDiscount_printsItsTotal() {
        val receipt = template(listOf(CartLine(drug(), qty = 3, discount = Money(2.0))))

        assertEquals(6.0, receipt.itemDiscountTotal)
        assertEquals(30.0, receipt.subtotal)
    }
}
