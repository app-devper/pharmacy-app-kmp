package app.devper.pharm.data.contract

import app.devper.pharm.data.network.AppJson
import app.devper.pharm.data.remote.dto.SaleItemDto
import app.devper.pharm.data.remote.dto.SaleRequest
import app.devper.pharm.data.remote.dto.SaleResponse
import app.devper.pharm.data.repository.internal.toDomain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SaleApiContractTest {

    private val saleItemWithSplitsJson = """
        {
          "id": "665f1e0aa1b2c3d4e5f60101",
          "sale_id": "665f1e0aa1b2c3d4e5f60100",
          "drug_id": "665f1e0aa1b2c3d4e5f60001",
          "drug_name": "Amlodipine 10mg",
          "qty": 10,
          "price": 8.0,
          "original_price": 8.0,
          "item_discount": 0.0,
          "subtotal": 80.0,
          "cost_subtotal": 45.0,
          "unit": "เม็ด",
          "unit_factor": 0,
          "price_tier": "",
          "lot_splits": [
            {"lot_id": "665f1e0aa1b2c3d4e5f60077", "lot_number": "L2406A", "expiry_date": "2027-06-30T00:00:00Z", "qty": 6},
            {"lot_id": "000000000000000000000000", "lot_number": "ADJUST:นับสต็อก", "expiry_date": "0001-01-01T00:00:00Z", "qty": 4}
          ],
          "oversold_qty": 0
        }
    """.trimIndent()

    @Test
    fun sale_item_lot_splits_map_and_synthetic_adjust_split_is_excluded_from_lot_bound() {
        val snapshot = AppJson.decodeFromString<SaleItemDto>(saleItemWithSplitsJson).toDomain(returnedQty = 1)
        assertEquals(10, snapshot.qty)
        assertEquals(6, snapshot.lotBoundQty)
        assertEquals(5, snapshot.returnableQty)
        assertEquals(4, snapshot.unreturnableQty)
    }

    @Test
    fun legacy_sale_item_without_lot_splits_is_not_returnable() {
        val json = """{"id": "a", "drug_id": "d", "drug_name": "X", "qty": 3, "price": 5.0}"""
        val snapshot = AppJson.decodeFromString<SaleItemDto>(json).toDomain(returnedQty = 0)
        assertEquals(0, snapshot.lotBoundQty)
        assertEquals(0, snapshot.returnableQty)
    }

    @Test
    fun checkout_response_matches_backend_sale_response_struct() {
        val json = """
            {
              "id": "665f1e0aa1b2c3d4e5f60100",
              "bill_no": "INV-260711-0042",
              "discount": 5.0,
              "total": 75.0,
              "change": 25.0,
              "stock_updates": [{"drug_id": "665f1e0aa1b2c3d4e5f60001", "new_stock": 110}]
            }
        """.trimIndent()
        val response = AppJson.decodeFromString<SaleResponse>(json)
        assertEquals("INV-260711-0042", response.billNo)
        assertEquals(110, response.stockUpdates[0].newStock)
        assertFalse(response.kySkippedByCashier)
    }

    @Test
    fun checkout_request_carries_ky_skip_flag_only_when_true() {
        val base = SaleRequest(items = emptyList(), received = 100.0)
        val skipped = base.copy(kySkippedByCashier = true)
        assertFalse("ky_skipped_by_cashier" in AppJson.encodeToString(SaleRequest.serializer(), base))
        assertTrue("\"ky_skipped_by_cashier\":true" in AppJson.encodeToString(SaleRequest.serializer(), skipped))
    }
}
