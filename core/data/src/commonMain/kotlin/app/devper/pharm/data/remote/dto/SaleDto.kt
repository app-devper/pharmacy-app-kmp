package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaleRequest(
    @SerialName("items") val items: List<SaleItemRequest>,
    @SerialName("received") val received: Double,
    @SerialName("discount") val discount: Double = 0.0,
    @SerialName("customer_id") val customerId: String? = null,
    @SerialName("client_request_id") val clientRequestId: String? = null,
    @SerialName("ky_skipped_by_cashier") val kySkippedByCashier: Boolean = false,
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
