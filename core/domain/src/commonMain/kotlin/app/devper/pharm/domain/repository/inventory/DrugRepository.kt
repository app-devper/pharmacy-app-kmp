package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.param.AddDrugParam
import app.devper.pharm.domain.param.ReorderSuggestionsParam
import app.devper.pharm.domain.param.UpdateDrugParam

interface DrugRepository {
    suspend fun list(): List<Drug>

    suspend fun add(param: AddDrugParam): Drug

    suspend fun update(param: UpdateDrugParam)

    suspend fun bulkImport(drugs: List<AddDrugParam>): BulkImportResult

    suspend fun lowStock(): List<Drug>

    suspend fun reorderSuggestions(param: ReorderSuggestionsParam): List<ReorderSuggestion>
}
