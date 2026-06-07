package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

data class ReorderSuggestion(
    val drugId: String,
    val drugName: String,
    val unit: String,
    val currentStock: Quantity,
    val minStock: Quantity,
    val qtySold: Quantity,
    val avgDailySale: Double,
    val daysLeft: Double,
    val suggestedQty: Quantity,
    val costPrice: Money,
    val sellPrice: Money,
) {

    val isInfiniteDaysLeft: Boolean get() = daysLeft >= 9000
}
