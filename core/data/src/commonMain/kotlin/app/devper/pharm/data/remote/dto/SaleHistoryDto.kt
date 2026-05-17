package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaleSummaryDto(
    @SerialName("id") val id: String,
    @SerialName("bill_no") val billNo: String? = null,
    @SerialName("customer_name") val customerName: String = "",
    @SerialName("total") val total: Double = 0.0,
    @SerialName("discount") val discount: Double = 0.0,
    @SerialName("sold_at") val soldAt: String = "",
    @SerialName("voided") val voided: Boolean = false,
)

@Serializable
data class SaleItemDto(
    @SerialName("id") val id: String,
    @SerialName("drug_id") val drugId: String,
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("qty") val qty: Int = 0,
    @SerialName("price") val price: Double = 0.0,
    @SerialName("original_price") val originalPrice: Double = 0.0,
    @SerialName("item_discount") val itemDiscount: Double = 0.0,
    @SerialName("unit") val unit: String = "",
    @SerialName("unit_factor") val unitFactor: Int = 0,
    @SerialName("price_tier") val priceTier: String = "",
)

@Serializable
data class DrugReturnRequest(
    @SerialName("items") val items: List<DrugReturnItemRequest>,
    @SerialName("reason") val reason: String,
)

@Serializable
data class DrugReturnItemRequest(
    @SerialName("sale_item_id") val saleItemId: String,
    @SerialName("qty") val qty: Int,
)

@Serializable
data class DrugReturnDto(
    @SerialName("id") val id: String = "",
    @SerialName("return_no") val returnNo: String = "",
    @SerialName("sale_id") val saleId: String = "",
    @SerialName("items") val items: List<DrugReturnItemDto> = emptyList(),
    @SerialName("refund") val refund: Double = 0.0,
    @SerialName("reason") val reason: String = "",
)

@Serializable
data class DrugReturnItemDto(
    @SerialName("sale_item_id") val saleItemId: String,
    @SerialName("qty") val qty: Int = 0,
)
