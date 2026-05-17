package app.devper.pharm.domain.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Ky9DraftBuilderTest {

    @Test
    fun build_happy_path() {
        val r = Ky9DraftBuilder.build(
            date = " 2026-04-01 ",
            drugName = " Para ",
            regNo = " RX-1 ",
            unit = " เม็ด ",
            qty = "10",
            pricePerUnit = "1.5",
            seller = " ABC ",
            invoiceNo = " INV-1 ",
        ).getOrThrow()
        assertEquals("2026-04-01", r.date)
        assertEquals("Para", r.drugName)
        assertEquals("RX-1", r.regNo)
        assertEquals("เม็ด", r.unit)
        assertEquals(10, r.qty)
        assertEquals(1.5, r.pricePerUnit)
        assertEquals("ABC", r.seller)
        assertEquals("INV-1", r.invoiceNo)
    }

    @Test
    fun build_rejects_blank_date() {
        assertTrue(Ky9DraftBuilder.build("", "x", "", "u", "1", "1", "", "").isFailure)
    }

    @Test
    fun build_rejects_blank_drugName() {
        assertTrue(Ky9DraftBuilder.build("2026-04-01", "", "", "u", "1", "1", "", "").isFailure)
    }

    @Test
    fun build_rejects_blank_unit() {
        assertTrue(Ky9DraftBuilder.build("2026-04-01", "x", "", "", "1", "1", "", "").isFailure)
    }

    @Test
    fun build_rejects_qty_zero_and_unparseable() {
        assertTrue(Ky9DraftBuilder.build("2026-04-01", "x", "", "u", "0", "1", "", "").isFailure)
        assertTrue(Ky9DraftBuilder.build("2026-04-01", "x", "", "u", "abc", "1", "", "").isFailure)
    }

    @Test
    fun build_rejects_negative_or_unparseable_price() {
        assertTrue(Ky9DraftBuilder.build("2026-04-01", "x", "", "u", "1", "-1", "", "").isFailure)
        assertTrue(Ky9DraftBuilder.build("2026-04-01", "x", "", "u", "1", "abc", "", "").isFailure)
    }

    @Test
    fun build_accepts_zero_price() {

        val r = Ky9DraftBuilder.build("2026-04-01", "x", "", "u", "1", "0", "", "").getOrThrow()
        assertEquals(0.0, r.pricePerUnit)
    }

    @Test
    fun isDraftValid_matches_build() {
        assertTrue(Ky9DraftBuilder.isDraftValid("2026-04-01", "x", "u", "1", "0"))
        assertFalse(Ky9DraftBuilder.isDraftValid("", "x", "u", "1", "0"))
        assertFalse(Ky9DraftBuilder.isDraftValid("2026-04-01", "", "u", "1", "0"))
        assertFalse(Ky9DraftBuilder.isDraftValid("2026-04-01", "x", "", "1", "0"))
        assertFalse(Ky9DraftBuilder.isDraftValid("2026-04-01", "x", "u", "0", "0"))
        assertFalse(Ky9DraftBuilder.isDraftValid("2026-04-01", "x", "u", "1", "-1"))
    }
}
