package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SaleRequest(
    @SerialName("items") val items: List<SaleItemRequest>,
    @SerialName("received") val received: Double,
    @SerialName("discount") val discount: Double = 0.0,
    @SerialName("customer_id") val customerId: String? = null,
    @SerialName("client_request_id") val clientRequestId: String? = null,
    @SerialName("ky_skipped_by_cashier") val kySkippedByCashier: Boolean = false,
    @SerialName("ky") val ky: SaleKyCaptureRequest? = null,
)

/** The bill's KY capture (pharmacy-api ADR-0011). */
@Serializable
data class SaleKyCaptureRequest(
    @SerialName("ky10") val ky10: Ky10CaptureRequest? = null,
    @SerialName("ky11") val ky11: Ky11CaptureRequest? = null,
    @SerialName("ky12") val ky12: Ky12CaptureRequest? = null,
)

@Serializable
data class Ky10CaptureRequest(
    @SerialName("buyer_name") val buyerName: String,
    @SerialName("buyer_address") val buyerAddress: String,
    @SerialName("rx_no") val rxNo: String,
    @SerialName("doctor") val doctor: String,
)

@Serializable
data class Ky11CaptureRequest(
    @SerialName("buyer_name") val buyerName: String,
    @SerialName("purpose") val purpose: String,
    @SerialName("pharmacist") val pharmacist: String,
)

@Serializable
data class Ky12CaptureRequest(
    @SerialName("rx_no") val rxNo: String,
    @SerialName("patient_name") val patientName: String,
    @SerialName("doctor") val doctor: String,
    @SerialName("hospital") val hospital: String,
    @SerialName("status") val status: String,
)

@Serializable
data class SaleItemRequest(
    @SerialName("drug_id") val drugId: String,
    @SerialName("qty") val qty: Int,
    @SerialName("price") val price: Double,
    @SerialName("original_price") val originalPrice: Double,
    @SerialName("item_discount") val itemDiscount: Double = 0.0,
    @SerialName("unit") val unit: String = "",
    @SerialName("unit_factor") val unitFactor: Int = 0,
    @SerialName("price_tier") val priceTier: String = "",
    @SerialName("allow_oversell") val allowOversell: Boolean = false,
)

@Serializable
data class SaleResponse(
    @SerialName("id") val id: String = "",
    @SerialName("bill_no") val billNo: String,
    @SerialName("total") val total: Double,
    @SerialName("change") val change: Double,
    @SerialName("discount") val discount: Double,
    @SerialName("stock_updates") val stockUpdates: List<StockUpdateDto> = emptyList(),
    @SerialName("ky_skipped_by_cashier") val kySkippedByCashier: Boolean = false,
)

@Serializable
data class StockUpdateDto(
    @SerialName("drug_id") val drugId: String,
    @SerialName("new_stock") val newStock: Int,
)

@Serializable
data class VoidSaleRequest(
    @SerialName("reason") val reason: String,
)

/** POST /sales/abandon (pharmacy-api ADR-0009). [payload] is kept verbatim for audit. */
@Serializable
data class AbandonRequest(
    @SerialName("client_request_id") val clientRequestId: String,
    @SerialName("kind") val kind: String,
    @SerialName("payload") val payload: JsonElement,
    @SerialName("reason") val reason: String,
)

/** The 409 body when the queued sale was recorded before it could be abandoned. */
@Serializable
data class AlreadyRecordedResponse(
    @SerialName("sale") val sale: SaleResponse? = null,
)
