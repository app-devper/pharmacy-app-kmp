package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.internal.parseLocalDateTimeOrNull
import app.devper.pharm.data.remote.dto.StockCountDto
import app.devper.pharm.data.remote.dto.StockCountInputDto
import app.devper.pharm.data.remote.dto.StockCountInputItemDto
import app.devper.pharm.data.remote.dto.StockCountItemDto
import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.domain.model.StockCountLine
import app.devper.pharm.domain.param.inventory.CreateStockCountParam

internal fun StockCountDto.toDomain(): StockCount = StockCount(
    id = id,
    countNo = countNo,
    note = note,
    items = items.map { it.toLine() },
    createdAt = createdAt.parseLocalDateTimeOrNull(),
)

internal fun CreateStockCountParam.toDto(): StockCountInputDto = StockCountInputDto(
    note = note.trim(),
    items = items.map { StockCountInputItemDto(drugId = it.drugId, counted = it.counted) },
)

private fun StockCountItemDto.toLine(): StockCountLine = StockCountLine(
    drugId = drugId,
    drugName = drugName,
    unit = unit,
    systemStock = systemStock,
    counted = counted,
    delta = delta,
)
