package app.devper.pharm.domain.extension

import app.devper.pharm.domain.validation.buildPurchaseOrderItemInput
import app.devper.pharm.domain.validation.isPurchaseOrderLineValid

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PurchaseOrderInputExtTest {

    @Test
    fun build_happy_path_returns_typed_input() {
        val r = buildPurchaseOrderItemInput(
            drugId = "d1",
            drugName = "  Paracetamol  ",
            lotNumber = " LOT-1 ",
            expiryDate = " 2027-01-01 ",
            qty = "10",
            costPrice = "1.5",
            sellPrice = "2.0",
        ).getOrThrow()
        assertEquals("d1", r.drugId)
        assertEquals("Paracetamol", r.drugName)
        assertEquals("LOT-1", r.lotNumber)
        assertEquals(kotlinx.datetime.LocalDate.parse("2027-01-01"), r.expiryDate)
        assertEquals(Quantity(10), r.qty)
        assertEquals(Money(1.5), r.costPrice)
        assertEquals(Money(2.0), r.sellPrice)
    }

    @Test
    fun build_blank_sellPrice_falls_back_to_null() {
        val r = buildPurchaseOrderItemInput(
            drugId = "d1", drugName = "x", lotNumber = "L", expiryDate = "2027-01-01",
            qty = "1", costPrice = "1", sellPrice = "",
        ).getOrThrow()
        assertNull(r.sellPrice)
    }

    @Test
    fun build_blank_costPrice_falls_back_to_zero() {
        val r = buildPurchaseOrderItemInput(
            drugId = "d1", drugName = "x", lotNumber = "L", expiryDate = "2027-01-01",
            qty = "1", costPrice = "", sellPrice = "",
        ).getOrThrow()
        assertEquals(Money.Zero, r.costPrice)
    }

    @Test
    fun build_rejects_blank_drugId() {
        val r = buildPurchaseOrderItemInput("", "x", "L", "2027-01-01", "1", "1", "")
        assertTrue(r.isFailure)
    }

    @Test
    fun build_rejects_blank_lot() {
        val r = buildPurchaseOrderItemInput("d1", "x", "", "2027-01-01", "1", "1", "")
        assertTrue(r.isFailure)
    }

    @Test
    fun build_rejects_blank_expiry() {
        val r = buildPurchaseOrderItemInput("d1", "x", "L", "", "1", "1", "")
        assertTrue(r.isFailure)
    }

    @Test
    fun build_rejects_unparseable_qty() {
        val r = buildPurchaseOrderItemInput("d1", "x", "L", "2027-01-01", "abc", "1", "")
        assertTrue(r.isFailure)
    }

    @Test
    fun build_rejects_qty_zero() {
        val r = buildPurchaseOrderItemInput("d1", "x", "L", "2027-01-01", "0", "1", "")
        assertTrue(r.isFailure)
    }

    @Test
    fun isLineValid_matches_build_outcome() {
        assertTrue(isPurchaseOrderLineValid("d1", "L", "2027-01-01", "1"))
        assertFalse(isPurchaseOrderLineValid("", "L", "2027-01-01", "1"))
        assertFalse(isPurchaseOrderLineValid("d1", "", "2027-01-01", "1"))
        assertFalse(isPurchaseOrderLineValid("d1", "L", "", "1"))
        assertFalse(isPurchaseOrderLineValid("d1", "L", "2027-01-01", "0"))
        assertFalse(isPurchaseOrderLineValid("d1", "L", "2027-01-01", "abc"))
        assertFalse(isPurchaseOrderLineValid("d1", "L", "31/12/2027", "1"))
        assertFalse(isPurchaseOrderLineValid("d1", "L", "not-a-date", "1"))
    }
}
