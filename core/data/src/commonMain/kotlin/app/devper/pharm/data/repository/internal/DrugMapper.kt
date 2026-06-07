package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.AltUnitDto
import app.devper.pharm.data.remote.dto.BulkImportResultDto
import app.devper.pharm.data.remote.dto.BulkImportRowErrorDto
import app.devper.pharm.data.remote.dto.DrugDto
import app.devper.pharm.data.remote.dto.ReorderSuggestionDto
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.model.BulkImportRowError
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ReorderSuggestion

internal fun DrugDto.toDomain(): Drug = Drug(
    id = id,
    name = name,
    genericName = genericName?.takeIf { it.isNotBlank() },
    type = type?.takeIf { it.isNotBlank() },
    strength = strength?.takeIf { it.isNotBlank() },
    barcode = barcode?.takeIf { it.isNotBlank() },
    sellPrice = sellPrice,
    costPrice = costPrice,
    stock = stock,
    minStock = minStock,
    unit = unit?.takeIf { it.isNotBlank() },
    regNo = regNo?.takeIf { it.isNotBlank() },
    prices = prices.orEmpty(),
    altUnits = altUnits.orEmpty().map { it.toDomain() },
    reportTypes = reportTypes.orEmpty(),
)

internal fun AltUnitDto.toDomain(): AltUnit = AltUnit(
    name = name,
    factor = factor,
    sellPrice = sellPrice,
    prices = prices.orEmpty(),
    barcode = barcode?.takeIf { it.isNotBlank() },
    hidden = hidden,
)

internal fun BulkImportResultDto.toDomain(): BulkImportResult = BulkImportResult(
    imported = imported,
    errors = errors.map { it.toDomain() },
)

internal fun BulkImportRowErrorDto.toDomain(): BulkImportRowError = BulkImportRowError(
    row = row,
    name = name,
    message = message,
)

internal fun ReorderSuggestionDto.toDomain(): ReorderSuggestion = ReorderSuggestion(
    drugId = drugId,
    drugName = drugName,
    unit = unit,
    currentStock = currentStock,
    minStock = minStock,
    qtySold = qtySold,
    avgDailySale = avgDailySale,
    daysLeft = daysLeft,
    suggestedQty = suggestedQty,
    costPrice = costPrice,
    sellPrice = sellPrice,
)
