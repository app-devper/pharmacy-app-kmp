package app.devper.pharm.domain.param.inventory

data class CreateStockCountParam(
    val note: String = "",
    val items: List<StockCountInputLine>,
)

data class StockCountInputLine(
    val drugId: String,
    val counted: Int,
)
