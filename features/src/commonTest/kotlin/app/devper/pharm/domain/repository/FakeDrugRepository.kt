package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.param.AddDrugParam
import app.devper.pharm.domain.param.ReorderSuggestionsParam
import app.devper.pharm.domain.param.UpdateDrugParam

class FakeDrugRepository(
    private val seed: List<Drug> = emptyList(),
    private val addThrowsOn: String? = null,
    private val updateThrowsOn: String? = null,
    private val listThrows: Boolean = false,
    private val lowStockSeed: List<Drug> = emptyList(),
    private val reorderSeed: List<ReorderSuggestion> = emptyList(),
) : DrugRepository {

    var listCallCount: Int = 0
        private set
    var lastAdd: AddDrugParam? = null
        private set
    var lastUpdate: UpdateDrugParam? = null
        private set
    var lastBulkImport: List<AddDrugParam>? = null
        private set
    var lastReorderParam: ReorderSuggestionsParam? = null
        private set

    override suspend fun list(): List<Drug> {
        listCallCount++
        if (listThrows) throw RuntimeException("list failed")
        return seed
    }

    override suspend fun add(param: AddDrugParam): Drug {
        if (addThrowsOn != null && param.barcode == addThrowsOn) {
            throw RuntimeException("backend rejected: $addThrowsOn")
        }
        lastAdd = param

        return Drug(
            id = "new-${param.barcode.ifBlank { param.name }}",
            name = param.name,
            genericName = param.genericName,
            type = param.type,
            strength = param.strength,
            barcode = param.barcode,
            sellPrice = param.sellPrice,
            costPrice = param.costPrice,
            stock = param.stock,
            minStock = param.minStock,
            unit = param.unit,
            regNo = param.regNo,
        )
    }

    override suspend fun update(param: UpdateDrugParam) {
        if (param.id == updateThrowsOn) throw RuntimeException("backend rejected: $updateThrowsOn")
        lastUpdate = param
    }

    override suspend fun bulkImport(drugs: List<AddDrugParam>): BulkImportResult {
        lastBulkImport = drugs
        return BulkImportResult(imported = drugs.size, errors = emptyList())
    }

    override suspend fun lowStock(): List<Drug> = lowStockSeed

    override suspend fun reorderSuggestions(param: ReorderSuggestionsParam): List<ReorderSuggestion> {
        lastReorderParam = param
        return reorderSeed
    }
}
