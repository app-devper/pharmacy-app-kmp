package app.devper.pharm.domain.param.inventory

import app.devper.pharm.domain.model.LotTarget

data class CreateStockCountParam(
    val note: String = "",
    val items: List<StockCountInputLine>,
)

data class StockCountInputLine(
    val drugId: String,
    val counted: Int,
    val lot: LotTarget? = null,
)
