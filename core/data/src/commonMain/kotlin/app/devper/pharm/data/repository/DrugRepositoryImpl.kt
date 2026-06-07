package app.devper.pharm.data.repository

import app.devper.pharm.data.internal.toIso
import app.devper.pharm.data.remote.api.DrugApi
import app.devper.pharm.data.remote.dto.AltUnitDto
import app.devper.pharm.data.remote.dto.BulkDrugImportInputDto
import app.devper.pharm.data.remote.dto.CreateLotDto
import app.devper.pharm.data.remote.dto.DrugInputDto
import app.devper.pharm.data.remote.dto.DrugUpdateDto
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.param.AddDrugParam
import app.devper.pharm.domain.param.CreateLotPayload
import app.devper.pharm.domain.param.ReorderSuggestionsParam
import app.devper.pharm.domain.param.UpdateDrugParam
import app.devper.pharm.domain.repository.DrugRepository

class DrugRepositoryImpl(
    private val api: DrugApi,
    private val stockChangeBus: StockChangeBus,
) : DrugRepository {

    override suspend fun list(): List<Drug> = api.list().map { it.toDomain() }

    override suspend fun add(param: AddDrugParam): Drug {
        val drug = api.add(param.toRequest()).toDomain()
        stockChangeBus.emit()
        return drug
    }

    override suspend fun update(param: UpdateDrugParam) {
        api.update(param.id, param.toRequest())
        stockChangeBus.emit()
    }

    override suspend fun bulkImport(drugs: List<AddDrugParam>): BulkImportResult {
        val response = api.bulkImport(BulkDrugImportInputDto(drugs.map { it.toRequest() }))

        if (response.imported > 0) stockChangeBus.emit()
        return response.toDomain()
    }

    override suspend fun lowStock(): List<Drug> = api.lowStock().map { it.toDomain() }

    override suspend fun reorderSuggestions(param: ReorderSuggestionsParam): List<ReorderSuggestion> =
        api.reorderSuggestions(param.days, param.lookahead).map { it.toDomain() }

    private fun AddDrugParam.toRequest() = DrugInputDto(
        name = name,
        genericName = genericName,
        type = type,
        strength = strength,
        barcode = barcode,
        sellPrice = sellPrice,
        costPrice = costPrice,
        stock = stock,
        minStock = minStock,
        regNo = regNo,
        unit = unit,
        reportTypes = reportTypes,
        altUnits = altUnits.map { it.toDto() },
        prices = prices,
        createLot = createLot?.toDto(),
    )

    private fun UpdateDrugParam.toRequest() = DrugUpdateDto(
        name = name,
        genericName = genericName,
        type = type,
        strength = strength,
        barcode = barcode,
        sellPrice = sellPrice,
        costPrice = costPrice,
        minStock = minStock,
        regNo = regNo,
        unit = unit,
        reportTypes = reportTypes,
        altUnits = altUnits.map { it.toDto() },
        prices = prices,
    )

    private fun AltUnit.toDto() = AltUnitDto(
        name = name,
        factor = factor,
        sellPrice = sellPrice,
        prices = prices,
        barcode = barcode,
        hidden = hidden,
    )

    private fun CreateLotPayload.toDto() = CreateLotDto(
        lotNumber = lotNumber,
        expiryDate = expiryDate.toIso(),
        importDate = importDate?.toIso(),
        costPrice = costPrice,
        sellPrice = sellPrice,
        quantity = quantity,
    )
}
