package app.devper.pharm.domain.repository.inventory

import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.param.inventory.AddDrugParam
import app.devper.pharm.domain.param.inventory.ReorderSuggestionsParam
import app.devper.pharm.domain.param.inventory.UpdateDrugParam

interface DrugRepository {
    suspend fun list(): List<Drug>

    suspend fun add(param: AddDrugParam): Drug

    suspend fun update(param: UpdateDrugParam)

    suspend fun bulkImport(drugs: List<AddDrugParam>): BulkImportResult

    suspend fun lowStock(): List<Drug>

    suspend fun reorderSuggestions(param: ReorderSuggestionsParam): List<ReorderSuggestion>
}
