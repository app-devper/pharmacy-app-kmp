package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PricedBillTest {

    private fun drug(sellPrice: Money = Money(10.0)) = Drug(
        id = "d1",
        name = "Amoxicillin",
        genericName = null,
        type = null,
        strength = null,
        barcode = null,
        sellPrice = sellPrice,
        costPrice = Money(0.0),
        stock = Quantity(1000),
        minStock = Quantity(0),
        unit = "เม็ด",
        regNo = null,
    )

    private fun percentBill() =
        priceBill(listOf(CartLine(drug(Money(123.45)), qty = 1)), CartDiscount.Percent(7.0))

    @Test
    fun emptyCart_isAllZero() {
        val bill = priceBill(emptyList(), CartDiscount.None)

        assertEquals(PricedBill.Empty, bill)
        assertEquals(Money.Zero, bill.grossSubtotal)
        assertEquals(Money.Zero, bill.subtotal)
        assertEquals(Money.Zero, bill.itemDiscountTotal)
        assertEquals(Money.Zero, bill.cartDiscountAmount)
        assertEquals(Money.Zero, bill.total)
    }

    @Test
    fun undiscountedPack_hasExactlyZeroItemDiscount() {
        val pack = AltUnit(name = "แผง", factor = 6, sellPrice = Money(35.0))
        val bill = priceBill(listOf(CartLine(drug(), qty = 18, selectedUnit = pack)), CartDiscount.None)

        assertEquals(Money(104.94), bill.grossSubtotal)
        assertEquals(bill.subtotal, bill.grossSubtotal)
        assertEquals(Money.Zero, bill.itemDiscountTotal)
        assertTrue(bill.itemDiscountTotal.isZero)
    }

    @Test
    fun altUnitLine_countsGrossOncePerDisplayUnit() {
        val box = AltUnit(name = "กล่อง", factor = 10, sellPrice = Money(100.0))
        val bill = priceBill(listOf(CartLine(drug(), qty = 20, selectedUnit = box)), CartDiscount.None)

        assertEquals(Money(200.0), bill.grossSubtotal)
    }

    @Test
    fun lineDiscount_showsAsItemDiscountTotal() {
        val line = CartLine(drug(Money(10.0)), qty = 3, discount = Money(1.0))
        val bill = priceBill(listOf(line), CartDiscount.None)

        assertEquals(Money(30.0), bill.grossSubtotal)
        assertEquals(Money(27.0), bill.subtotal)
        assertEquals(Money(3.0), bill.itemDiscountTotal)
    }

    @Test
    fun percentDiscount_isRoundedToTheSatang() {
        val bill = percentBill()

        assertEquals(Money(123.45), bill.subtotal)
        assertEquals(Money(8.64), bill.cartDiscountAmount)
        assertEquals(Money(114.81), bill.total)
    }

    @Test
    fun flatDiscountAboveSubtotal_isClampedAndTotalIsZero() {
        val bill = priceBill(listOf(CartLine(drug(Money(10.0)), qty = 2)), CartDiscount.Flat(Money(500.0)))

        assertEquals(Money(20.0), bill.cartDiscountAmount)
        assertEquals(Money.Zero, bill.total)
    }

    @Test
    fun change_isRoundedAndNeverNegative() {
        val bill = percentBill()

        assertEquals(Money(0.19), bill.change(Money(115.0)))
        assertEquals(Money.Zero, bill.change(Money(100.0)))
    }
}
