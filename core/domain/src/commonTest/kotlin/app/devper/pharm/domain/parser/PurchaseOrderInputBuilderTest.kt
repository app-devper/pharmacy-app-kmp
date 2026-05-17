package app.devper.pharm.domain.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PurchaseOrderInputBuilderTest {

    @Test
    fun build_happy_path_returns_typed_input() {
        val r = PurchaseOrderInputBuilder.build(
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
        assertEquals("2027-01-01", r.expiryDate)
        assertEquals(10, r.qty)
        assertEquals(1.5, r.costPrice)
        assertEquals(2.0, r.sellPrice)
    }

    @Test
    fun build_blank_sellPrice_falls_back_to_null() {
        val r = PurchaseOrderInputBuilder.build(
            drugId = "d1", drugName = "x", lotNumber = "L", expiryDate = "2027-01-01",
            qty = "1", costPrice = "1", sellPrice = "",
        ).getOrThrow()
        assertNull(r.sellPrice)
    }

    @Test
    fun build_blank_costPrice_falls_back_to_zero() {
        val r = PurchaseOrderInputBuilder.build(
            drugId = "d1", drugName = "x", lotNumber = "L", expiryDate = "2027-01-01",
            qty = "1", costPrice = "", sellPrice = "",
        ).getOrThrow()
        assertEquals(0.0, r.costPrice)
    }

    @Test
    fun build_rejects_blank_drugId() {
        val r = PurchaseOrderInputBuilder.build("", "x", "L", "2027-01-01", "1", "1", "")
        assertTrue(r.isFailure)
    }

    @Test
    fun build_rejects_blank_lot() {
        val r = PurchaseOrderInputBuilder.build("d1", "x", "", "2027-01-01", "1", "1", "")
        assertTrue(r.isFailure)
    }

    @Test
    fun build_rejects_blank_expiry() {
        val r = PurchaseOrderInputBuilder.build("d1", "x", "L", "", "1", "1", "")
        assertTrue(r.isFailure)
    }

    @Test
    fun build_rejects_unparseable_qty() {
        val r = PurchaseOrderInputBuilder.build("d1", "x", "L", "2027-01-01", "abc", "1", "")
        assertTrue(r.isFailure)
    }

    @Test
    fun build_rejects_qty_zero() {
        val r = PurchaseOrderInputBuilder.build("d1", "x", "L", "2027-01-01", "0", "1", "")
        assertTrue(r.isFailure)
    }

    @Test
    fun isLineValid_matches_build_outcome() {
        assertTrue(PurchaseOrderInputBuilder.isLineValid("d1", "L", "2027-01-01", "1"))
        assertFalse(PurchaseOrderInputBuilder.isLineValid("", "L", "2027-01-01", "1"))
        assertFalse(PurchaseOrderInputBuilder.isLineValid("d1", "", "2027-01-01", "1"))
        assertFalse(PurchaseOrderInputBuilder.isLineValid("d1", "L", "", "1"))
        assertFalse(PurchaseOrderInputBuilder.isLineValid("d1", "L", "2027-01-01", "0"))
        assertFalse(PurchaseOrderInputBuilder.isLineValid("d1", "L", "2027-01-01", "abc"))
    }
}
