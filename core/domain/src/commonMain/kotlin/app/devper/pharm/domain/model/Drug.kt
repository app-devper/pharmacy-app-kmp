package app.devper.pharm.domain.model

data class Drug(
    val id: String,
    val name: String,
    val genericName: String?,
    val type: String?,
    val strength: String?,
    val barcode: String?,
    val sellPrice: Double,
    val costPrice: Double,
    val stock: Int,
    val minStock: Int,
    val unit: String?,
    val regNo: String?,

    val prices: Map<String, Double> = emptyMap(),

    val altUnits: List<AltUnit> = emptyList(),

    val reportTypes: List<String> = emptyList(),
) {
    val stockStatus: StockStatus = when {
        stock <= 0 -> StockStatus.OutOrOversold
        minStock > 0 && stock <= minStock -> StockStatus.Low
        else -> StockStatus.Healthy
    }
}

enum class StockStatus { Healthy, Low, OutOrOversold }
