package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.param.AddDrugParam
import app.devper.pharm.domain.param.ReorderSuggestionsParam
import app.devper.pharm.domain.param.UpdateDrugParam

class FakeDrugRepositoryForBulk(
    private val result: BulkImportResult = BulkImportResult(imported = 0, errors = emptyList()),
) : DrugRepository {

    var lastBulkImport: List<AddDrugParam>? = null
        private set

    override suspend fun list(): List<Drug> = emptyList()
    override suspend fun add(param: AddDrugParam): Drug =
        throw NotImplementedError("not under test")
    override suspend fun update(param: UpdateDrugParam) =
        throw NotImplementedError("not under test")

    override suspend fun bulkImport(drugs: List<AddDrugParam>): BulkImportResult {
        lastBulkImport = drugs
        return result
    }

    override suspend fun lowStock(): List<Drug> = emptyList()
    override suspend fun reorderSuggestions(param: ReorderSuggestionsParam): List<ReorderSuggestion> =
        emptyList()
}
