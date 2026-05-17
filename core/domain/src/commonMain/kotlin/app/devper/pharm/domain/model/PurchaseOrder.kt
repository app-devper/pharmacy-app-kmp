package app.devper.pharm.domain.model

data class PurchaseOrder(
    val id: String,
    val docNo: String,
    val supplier: String,
    val invoiceNo: String,
    val receiveDate: String,
    val items: List<PurchaseOrderItem>,
    val itemCount: Int,
    val totalCost: Double,
    val status: PurchaseOrderStatus,
    val notes: String,
    val createdAt: String,
    val confirmedAt: String?,
)

data class PurchaseOrderSummary(
    val id: String,
    val docNo: String,
    val supplier: String,
    val invoiceNo: String,
    val receiveDate: String,
    val itemCount: Int,
    val totalCost: Double,
    val status: PurchaseOrderStatus,
    val notes: String,
    val createdAt: String,
    val confirmedAt: String?,
)

data class PurchaseOrderItem(
    val drugId: String,
    val drugName: String,
    val lotNumber: String,
    val expiryDate: String,
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
