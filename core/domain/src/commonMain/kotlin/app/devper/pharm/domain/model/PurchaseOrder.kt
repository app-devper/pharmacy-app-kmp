package app.devper.pharm.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class PurchaseOrder(
    val id: String,
    val docNo: String,
    val supplier: String,
    val invoiceNo: String,
    val receiveDate: LocalDate?,
    val items: List<PurchaseOrderItem>,
    val itemCount: Int,
    val totalCost: Double,
    val status: PurchaseOrderStatus,
    val notes: String,
    val createdAt: LocalDateTime?,
    val confirmedAt: LocalDateTime?,
)

data class PurchaseOrderSummary(
    val id: String,
    val docNo: String,
    val supplier: String,
    val invoiceNo: String,
    val receiveDate: LocalDate?,
    val itemCount: Int,
    val totalCost: Double,
    val status: PurchaseOrderStatus,
    val notes: String,
    val createdAt: LocalDateTime?,
    val confirmedAt: LocalDateTime?,
)

data class PurchaseOrderItem(
    val drugId: String,
    val drugName: String,
    val lotNumber: String,
    val expiryDate: LocalDate?,
    val qty: Int,
    val costPrice: Double,
    val sellPrice: Double?,
)

enum class PurchaseOrderStatus(val wire: String) {
    Draft("draft"),
    Confirmed("confirmed");

    companion object {
        fun fromWire(value: String?): PurchaseOrderStatus =
            entries.firstOrNull { it.wire == value } ?: Draft
    }
}
