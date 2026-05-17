package app.devper.pharm.domain.util

import app.devper.pharm.domain.model.SaleItemSnapshot

object SaleReturnQty {

    fun resolve(item: SaleItemSnapshot, displayQty: Int): Int {
        val factor = if (item.unitFactor > 1) item.unitFactor else 1
        return (displayQty * factor).coerceIn(0, item.remainingQty)
    }
}
