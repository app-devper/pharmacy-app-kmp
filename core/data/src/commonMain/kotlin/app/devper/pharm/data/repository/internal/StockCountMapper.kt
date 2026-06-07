package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.internal.parseLocalDateTimeOrNull
import app.devper.pharm.data.remote.dto.StockCountDto
import app.devper.pharm.data.remote.dto.StockCountItemDto
import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.domain.model.StockCountLine

internal fun StockCountDto.toDomain(): StockCount = StockCount(
    id = id,
    countNo = countNo,
    note = note,
    items = items.map { it.toLine() },
    createdAt = createdAt.parseLocalDateTimeOrNull(),
)

private fun StockCountItemDto.toLine(): StockCountLine = StockCountLine(
    drugId = drugId,
    drugName = drugName,
    unit = unit,
    systemStock = systemStock,
    counted = counted,
    delta = delta,
)
