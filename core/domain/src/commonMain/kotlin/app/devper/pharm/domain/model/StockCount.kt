package app.devper.pharm.domain.model

import kotlinx.datetime.LocalDateTime

data class StockCount(
    val id: String,
    val countNo: String,
    val note: String,
    val items: List<StockCountLine>,
    val createdAt: LocalDateTime?,
)

data class StockCountLine(
    val drugId: String,
    val drugName: String,
    val unit: String,
    val systemStock: Int,
    val counted: Int,
    val delta: Int,
)
