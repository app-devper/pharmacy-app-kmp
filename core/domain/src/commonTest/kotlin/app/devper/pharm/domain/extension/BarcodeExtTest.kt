package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.Drug
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BarcodeExtTest {

    private fun drug(
        id: String,
        name: String = id,
        barcode: String? = null,
        regNo: String? = null,
        altUnits: List<AltUnit> = emptyList(),
    ) = Drug(
        id = id, name = name, genericName = null, type = null, strength = null,
        barcode = barcode, sellPrice = 1.0, costPrice = 0.0, stock = 0, minStock = 0,
        unit = "ชิ้น", regNo = regNo, altUnits = altUnits,
    )

    @Test
    fun returns_null_for_blank_code() {
        assertNull(listOf(drug("d1")).matchBarcode("  "))
    }

    @Test
    fun matches_visible_alt_unit_barcode_first() {
        val alt = AltUnit(name = "กล่อง", factor = 10, sellPrice = 0.0, barcode = "ALT-1", hidden = false)
        val d = drug("d1", barcode = "PRIMARY-1", altUnits = listOf(alt))
        val match = listOf(d).matchBarcode("ALT-1")
        assertEquals("d1", match?.drug?.id)
        assertEquals(alt, match?.altUnit)
    }

    @Test
    fun skips_hidden_alt_unit_barcode() {
        val hidden = AltUnit(name = "กล่อง", factor = 10, sellPrice = 0.0, barcode = "ALT-1", hidden = true)
        val d = drug("d1", barcode = "PRIMARY-1", altUnits = listOf(hidden))

        val match = listOf(d).matchBarcode("ALT-1")
        assertNull(match)
    }

    @Test
    fun falls_back_to_primary_drug_barcode() {
        val match = listOf(drug("d1", barcode = "8851001")).matchBarcode("8851001")
        assertEquals("d1", match?.drug?.id)
        assertNull(match?.altUnit)
    }

    @Test
    fun final_fallback_is_regNo() {
        val match = listOf(drug("d1", regNo = "FDA-12345")).matchBarcode("FDA-12345")
        assertEquals("d1", match?.drug?.id)
        assertNull(match?.altUnit)
    }

    @Test
    fun returns_null_when_nothing_matches() {
        assertNull(listOf(drug("d1", barcode = "x", regNo = "y")).matchBarcode("nope"))
    }

    @Test
    fun trims_whitespace_around_code() {
        val match = listOf(drug("d1", barcode = "8851001")).matchBarcode("  8851001  ")
        assertEquals("d1", match?.drug?.id)
    }
}
