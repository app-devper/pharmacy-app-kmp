package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.SaleItemSnapshot

fun SaleItemSnapshot.resolveReturnQty(displayQty: Int): Int {
    val factor = if (unitFactor > 1) unitFactor else 1
    return (displayQty * factor).coerceIn(0, remainingQty)
}
