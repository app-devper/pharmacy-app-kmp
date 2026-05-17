package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PurchaseOrderDto(
    @SerialName("id") val id: String,
    @SerialName("doc_no") val docNo: String,
    @SerialName("supplier") val supplier: String = "",
    @SerialName("invoice_no") val invoiceNo: String = "",
    @SerialName("receive_date") val receiveDate: String = "",
    @SerialName("items") val items: List<PurchaseOrderItemDto> = emptyList(),
    @SerialName("item_count") val itemCount: Int = 0,
    @SerialName("total_cost") val totalCost: Double = 0.0,
    @SerialName("status") val status: String = "draft",
    @SerialName("notes") val notes: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("confirmed_at") val confirmedAt: String? = null,
)

@Serializable
data class PurchaseOrderSummaryDto(
    @SerialName("id") val id: String,
    @SerialName("doc_no") val docNo: String,
    @SerialName("supplier") val supplier: String = "",
    @SerialName("invoice_no") val invoiceNo: String = "",
    @SerialName("receive_date") val receiveDate: String = "",
    @SerialName("item_count") val itemCount: Int = 0,
    @SerialName("total_cost") val totalCost: Double = 0.0,
    @SerialName("status") val status: String = "draft",
    @SerialName("notes") val notes: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("confirmed_at") val confirmedAt: String? = null,
)

@Serializable
data class PurchaseOrderItemDto(
    @SerialName("drug_id") val drugId: String,
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("lot_number") val lotNumber: String = "",
    @SerialName("expiry_date") val expiryDate: String = "",
    @SerialName("qty") val qty: Int = 0,
    @SerialName("cost_price") val costPrice: Double = 0.0,
    @SerialName("sell_price") val sellPrice: Double? = null,
)

@Serializable
data class PurchaseOrderInputDto(
    @SerialName("supplier") val supplier: String = "",
    @SerialName("invoice_no") val invoiceNo: String = "",
    @SerialName("receive_date") val receiveDate: String = "",
    @SerialName("notes") val notes: String = "",
    @SerialName("items") val items: List<PurchaseOrderItemInputDto> = emptyList(),
)

@Serializable
data class PurchaseOrderItemInputDto(
    @SerialName("drug_id") val drugId: String,
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("lot_number") val lotNumber: String,
    @SerialName("expiry_date") val expiryDate: String,
    @SerialName("qty") val qty: Int,
    @SerialName("cost_price") val costPrice: Double,
    @SerialName("sell_price") val sellPrice: Double? = null,
)
