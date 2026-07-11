package app.devper.pharm.data.contract

import app.devper.pharm.data.network.AppJson
import app.devper.pharm.data.remote.dto.ExpiringLotDto
import app.devper.pharm.data.remote.dto.DrugLotDto
import app.devper.pharm.data.repository.internal.toDomain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InventoryApiContractTest {

    @Test
    fun expiring_lot_payload_keeps_negative_days_left() {
        val json = """
            {
              "id": "665f1e0aa1b2c3d4e5f60077",
              "drug_id": "665f1e0aa1b2c3d4e5f60001",
              "drug_name": "Amlodipine 10mg",
              "lot_number": "L2312B",
              "expiry_date": "2026-07-01T00:00:00Z",
              "remaining": 14,
              "days_left": -10
            }
        """.trimIndent()
        val lot = AppJson.decodeFromString<ExpiringLotDto>(json).toDomain()
        assertEquals(-10, lot.daysLeft)
        assertEquals(14, lot.remaining)
        assertEquals("L2312B", lot.lotNumber)
    }

    @Test
    fun lot_payload_with_null_price_overrides_decodes_as_null() {
        val json = """
            {
              "id": "665f1e0aa1b2c3d4e5f60077",
              "drug_id": "665f1e0aa1b2c3d4e5f60001",
              "lot_number": "L2406A",
              "expiry_date": "2027-06-30T00:00:00Z",
              "import_date": "2026-06-01T00:00:00Z",
              "cost_price": null,
              "sell_price": null,
              "quantity": 100,
              "remaining": 60
            }
        """.trimIndent()
        val dto = AppJson.decodeFromString<DrugLotDto>(json)
        assertNull(dto.costPrice)
        assertNull(dto.sellPrice)
        assertEquals(60, dto.remaining)
    }
}
