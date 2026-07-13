package app.devper.pharm.data.contract

import app.devper.pharm.data.network.AppJson
import app.devper.pharm.data.remote.dto.DrugDto
import app.devper.pharm.data.repository.internal.toDomain
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DrugApiContractTest {

    private val fullDrugJson = """
        {
          "id": "665f1e0aa1b2c3d4e5f60001",
          "name": "Amlodipine 10mg",
          "generic_name": "Amlodipine",
          "type": "cur",
          "strength": "10mg",
          "barcode": "8850123456789",
          "sell_price": 8.0,
          "cost_price": 4.5,
          "stock": 120,
          "min_stock": 30,
          "reg_no": "1A 123/45",
          "unit": "เม็ด",
          "report_types": ["ky11"],
          "alt_units": [
            {"name": "แผง", "factor": 10, "sell_price": 75.0, "prices": {"wholesale": 70.0}, "barcode": "8850123456790", "hidden": false}
          ],
          "prices": {"retail": 8.0, "regular": 7.5, "wholesale": 7.0},
          "next_lot": {"lot_id": "665f1e0aa1b2c3d4e5f60077", "lot_number": "L2406A", "expiry_date": "2027-06-30T00:00:00Z"},
          "created_at": "2026-05-01T09:30:00Z"
        }
    """.trimIndent()

    @Test
    fun full_drug_payload_decodes_and_maps() {
        val drug = AppJson.decodeFromString<DrugDto>(fullDrugJson).toDomain()
        assertEquals("665f1e0aa1b2c3d4e5f60001", drug.id)
        assertEquals(8.0, drug.sellPrice.amount)
        assertEquals(120, drug.stock.value)
        assertEquals(7.0, drug.prices["wholesale"]?.amount)
        assertEquals(1, drug.altUnits.size)
        assertEquals(10, drug.altUnits[0].factor)
        assertEquals("L2406A", drug.nextLotNumber)
        assertEquals(LocalDate(2027, 6, 30), drug.nextLotExpiry)
        assertTrue("ky11" in drug.reportTypes)
    }

    @Test
    fun legacy_drug_without_optional_blocks_decodes_with_defaults() {
        val json = """{"id": "665f1e0aa1b2c3d4e5f60002", "name": "Gauze", "sell_price": 15.0, "stock": 5}"""
        val drug = AppJson.decodeFromString<DrugDto>(json).toDomain()
        assertNull(drug.nextLotExpiry)
        assertNull(drug.nextLotNumber)
        assertTrue(drug.altUnits.isEmpty())
        assertTrue(drug.prices.isEmpty())
        assertNull(drug.genericName)
    }

    @Test
    fun unknown_server_fields_are_ignored() {
        val json = """{"id": "x", "name": "N", "sell_price": 1.0, "created_at": "2026-01-01T00:00:00Z", "future_field": {"nested": true}}"""
        val drug = AppJson.decodeFromString<DrugDto>(json).toDomain()
        assertEquals("N", drug.name)
    }
}
