package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.DrugApi
import app.devper.pharm.data.remote.dto.BulkDrugImportInputDto
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toRequest
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.param.AddDrugParam
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
}
