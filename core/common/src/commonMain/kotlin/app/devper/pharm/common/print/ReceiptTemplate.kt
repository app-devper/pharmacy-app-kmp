package app.devper.pharm.common.print

data class ReceiptTemplate(
    val storeName: String,
    val storeAddress: String,
    val storePhone: String,
    val storeTaxId: String,
    val billNo: String,
    val soldAt: String,
    val customerName: String,
    val items: List<ReceiptLine>,
    val subtotal: Double,
    val itemDiscountTotal: Double,
    val cartDiscount: Double,
    val total: Double,
    val received: Double,
    val change: Double,
    val pharmacistName: String,
    val footer: String,
)

data class ReceiptLine(
    val name: String,
    val displayQty: Int,
    val displayUnit: String,
    val unitPrice: Double,
    val lineTotal: Double,
)
