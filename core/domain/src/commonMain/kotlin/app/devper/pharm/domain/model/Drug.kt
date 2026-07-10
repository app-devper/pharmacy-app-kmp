package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import kotlinx.datetime.LocalDate

data class Drug(
    val id: String,
    val name: String,
    val genericName: String?,
    val type: String?,
    val strength: String?,
    val barcode: String?,
    val sellPrice: Money,
    val costPrice: Money,
    val stock: Quantity,
    val minStock: Quantity,
    val unit: String?,
    val regNo: String?,

    val nextLotNumber: String? = null,
    val nextLotExpiry: LocalDate? = null,

    val prices: Map<String, Money> = emptyMap(),

    val altUnits: List<AltUnit> = emptyList(),

    val reportTypes: List<String> = emptyList(),
) {
    val stockStatus: StockStatus = when {
        !stock.isPositive -> StockStatus.OutOrOversold
        minStock.isPositive && stock <= minStock -> StockStatus.Low
        else -> StockStatus.Healthy
    }
}

enum class StockStatus { Healthy, Low, OutOrOversold }
