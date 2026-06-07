package app.devper.pharm.domain.model

import kotlinx.datetime.LocalDateTime

data class SaleSummary(
    val id: String,
    val billNo: String,
    val customerName: String,
    val total: Double,
    val discount: Double,
    val soldAt: LocalDateTime?,
    val voided: Boolean,
)

data class SaleItemSnapshot(
    val id: String,
    val drugId: String,
    val drugName: String,
    val qty: Int,
    val price: Double,
    val originalPrice: Double,
    val itemDiscount: Double,
    val unit: String,
    val unitFactor: Int,
    val priceTier: String,

    val returnedQty: Int = 0,
) {
    val displayUnit: String get() = unit.ifBlank { "หน่วย" }
    val displayQty: Int
        get() = if (unitFactor > 1) qty / unitFactor else qty
    val remainingQty: Int get() = (qty - returnedQty).coerceAtLeast(0)
    val remainingDisplayQty: Int
        get() = if (unitFactor > 1) remainingQty / unitFactor else remainingQty
}
