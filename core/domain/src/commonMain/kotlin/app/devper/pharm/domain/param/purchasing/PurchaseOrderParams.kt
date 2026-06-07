package app.devper.pharm.domain.param

import kotlinx.datetime.LocalDate

data class AddPurchaseOrderParam(
    val supplier: String,
    val invoiceNo: String = "",
    val receiveDate: LocalDate? = null,
    val notes: String = "",
    val items: List<PurchaseOrderItemInput>,
)

data class UpdatePurchaseOrderParam(
    val id: String,
    val supplier: String,
    val invoiceNo: String = "",
    val receiveDate: LocalDate? = null,
    val notes: String = "",
    val items: List<PurchaseOrderItemInput>,
)

data class PurchaseOrderItemInput(
    val drugId: String,
    val drugName: String = "",
    val lotNumber: String,
    val expiryDate: LocalDate,
    val qty: Int,
    val costPrice: Double,
    val sellPrice: Double? = null,
)
