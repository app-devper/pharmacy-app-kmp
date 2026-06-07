package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.SaleResponse
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.model.StockUpdate

internal fun SaleResponse.toDomain(): Sale = Sale(
    id = id,
    billNo = billNo,
    total = total,
    change = change,
    discount = discount,
    stockUpdates = stockUpdates.map { StockUpdate(it.drugId, it.newStock) },
    kySkippedByCashier = kySkippedByCashier,
)
