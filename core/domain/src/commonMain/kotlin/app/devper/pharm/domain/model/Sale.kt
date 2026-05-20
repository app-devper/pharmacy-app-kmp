package app.devper.pharm.domain.model

data class Sale(

    val id: String,
    val billNo: String,
    val total: Double,
    val change: Double,
    val discount: Double,
    val stockUpdates: List<StockUpdate>,
    val kySkippedByCashier: Boolean = false,
)

data class StockUpdate(
    val drugId: String,
    val newStock: Int,
)
