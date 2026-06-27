package app.devper.pharm.domain.repository

import app.devper.pharm.domain.repository.inventory.DrugRepository

import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.param.inventory.AddDrugParam
import app.devper.pharm.domain.param.inventory.ReorderSuggestionsParam
import app.devper.pharm.domain.param.inventory.UpdateDrugParam

class FakeDrugRepositoryForBulk(
    private val result: BulkImportResult = BulkImportResult(imported = 0, errors = emptyList()),
    private val importThrows: Boolean = false,
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
        if (importThrows) throw RuntimeException("import failed")
        return result
    }

    override suspend fun lowStock(): List<Drug> = emptyList()
    override suspend fun reorderSuggestions(param: ReorderSuggestionsParam): List<ReorderSuggestion> =
        emptyList()
}
