package app.devper.pharm.domain.model

data class ReorderSuggestion(
    val drugId: String,
    val drugName: String,
    val unit: String,
    val currentStock: Int,
    val minStock: Int,
    val qtySold: Int,
    val avgDailySale: Double,
    val daysLeft: Double,
    val suggestedQty: Int,
    val costPrice: Double,
    val sellPrice: Double,
) {

    val isInfiniteDaysLeft: Boolean get() = daysLeft >= 9000
}
