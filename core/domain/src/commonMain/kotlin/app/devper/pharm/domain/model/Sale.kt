package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money

data class Sale(

    val id: String,
    val billNo: String,
    val total: Money,
    val change: Money,
    val discount: Money,
    val stockUpdates: List<StockUpdate>,
    val kySkippedByCashier: Boolean = false,
)

data class StockUpdate(
    val drugId: String,
    val newStock: Int,
)
