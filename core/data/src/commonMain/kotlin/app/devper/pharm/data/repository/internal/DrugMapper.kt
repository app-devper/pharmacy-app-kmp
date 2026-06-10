package app.devper.pharm.data.repository.internal

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.data.internal.toIso
import app.devper.pharm.data.remote.dto.AltUnitDto
import app.devper.pharm.data.remote.dto.BulkImportResultDto
import app.devper.pharm.data.remote.dto.BulkImportRowErrorDto
import app.devper.pharm.data.remote.dto.CreateLotDto
import app.devper.pharm.data.remote.dto.DrugDto
import app.devper.pharm.data.remote.dto.DrugInputDto
import app.devper.pharm.data.remote.dto.DrugUpdateDto
import app.devper.pharm.data.remote.dto.ReorderSuggestionDto
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.model.BulkImportRowError
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.param.inventory.AddDrugParam
import app.devper.pharm.domain.param.inventory.CreateLotPayload
import app.devper.pharm.domain.param.inventory.UpdateDrugParam

internal fun DrugDto.toDomain(): Drug = Drug(
    id = id,
    name = name,
    genericName = genericName?.takeIf { it.isNotBlank() },
    type = type?.takeIf { it.isNotBlank() },
    strength = strength?.takeIf { it.isNotBlank() },
    barcode = barcode?.takeIf { it.isNotBlank() },
    sellPrice = Money(sellPrice),
    costPrice = Money(costPrice),
    stock = Quantity(stock),
    minStock = Quantity(minStock),
    unit = unit?.takeIf { it.isNotBlank() },
    regNo = regNo?.takeIf { it.isNotBlank() },
    prices = prices.orEmpty().mapValues { Money(it.value) },
    altUnits = altUnits.orEmpty().map { it.toDomain() },
    reportTypes = reportTypes.orEmpty(),
)

internal fun AltUnitDto.toDomain(): AltUnit = AltUnit(
    name = name,
    factor = factor,
    sellPrice = Money(sellPrice),
    prices = prices.orEmpty().mapValues { Money(it.value) },
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
    currentStock = Quantity(currentStock),
    minStock = Quantity(minStock),
    qtySold = Quantity(qtySold),
    avgDailySale = avgDailySale,
    daysLeft = daysLeft,
    suggestedQty = Quantity(suggestedQty),
    costPrice = Money(costPrice),
    sellPrice = Money(sellPrice),
)

internal fun AddDrugParam.toRequest(): DrugInputDto = DrugInputDto(
    name = name,
    genericName = genericName,
    type = type,
    strength = strength,
    barcode = barcode,
    sellPrice = sellPrice.amount,
    costPrice = costPrice.amount,
    stock = stock.value,
    minStock = minStock.value,
    regNo = regNo,
    unit = unit,
    reportTypes = reportTypes,
    altUnits = altUnits.map { it.toDto() },
    prices = prices.mapValues { it.value.amount },
    createLot = createLot?.toDto(),
)

internal fun UpdateDrugParam.toRequest(): DrugUpdateDto = DrugUpdateDto(
    name = name,
    genericName = genericName,
    type = type,
    strength = strength,
    barcode = barcode,
    sellPrice = sellPrice.amount,
    costPrice = costPrice.amount,
    minStock = minStock.value,
    regNo = regNo,
    unit = unit,
    reportTypes = reportTypes,
    altUnits = altUnits.map { it.toDto() },
    prices = prices.mapValues { it.value.amount },
)

internal fun AltUnit.toDto(): AltUnitDto = AltUnitDto(
    name = name,
    factor = factor,
    sellPrice = sellPrice.amount,
    prices = prices.mapValues { it.value.amount },
    barcode = barcode,
    hidden = hidden,
)

internal fun CreateLotPayload.toDto(): CreateLotDto = CreateLotDto(
    lotNumber = lotNumber,
    expiryDate = expiryDate.toIso(),
    importDate = importDate?.toIso(),
    costPrice = costPrice?.amount,
    sellPrice = sellPrice?.amount,
    quantity = quantity.value,
)
