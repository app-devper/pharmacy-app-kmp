package app.devper.pharm.data.remote.dto

import app.devper.pharm.data.network.AppJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SaleDtoTest {

    @Test
    fun saleRequest_round_trip_preserves_ky_skipped_flag() {
        val original = SaleRequest(
            items = emptyList(),
            received = 100.0,
            kySkippedByCashier = true,
        )
        val encoded = AppJson.encodeToString(SaleRequest.serializer(), original)
        assertTrue(encoded.contains("\"ky_skipped_by_cashier\":true"))
        val decoded = AppJson.decodeFromString(SaleRequest.serializer(), encoded)
        assertEquals(true, decoded.kySkippedByCashier)
    }

    @Test
    fun saleRequest_defaults_ky_skipped_to_false_when_missing() {
        val raw = """{"items":[],"received":100.0}"""
        val decoded = AppJson.decodeFromString(SaleRequest.serializer(), raw)
        assertEquals(false, decoded.kySkippedByCashier)
    }

    @Test
    fun saleResponse_round_trip_preserves_ky_skipped_flag() {
        val original = SaleResponse(
            id = "s1",
            billNo = "INV-001",
            total = 100.0,
            change = 0.0,
            discount = 0.0,
            kySkippedByCashier = true,
        )
        val encoded = AppJson.encodeToString(SaleResponse.serializer(), original)
        assertTrue(encoded.contains("\"ky_skipped_by_cashier\":true"))
        val decoded = AppJson.decodeFromString(SaleResponse.serializer(), encoded)
        assertEquals(true, decoded.kySkippedByCashier)
    }

    @Test
    fun saleResponse_defaults_ky_skipped_to_false_when_missing() {
        val raw = """{"id":"s1","bill_no":"INV-001","total":100.0,"change":0.0,"discount":0.0}"""
        val decoded = AppJson.decodeFromString(SaleResponse.serializer(), raw)
        assertEquals(false, decoded.kySkippedByCashier)
    }
}
