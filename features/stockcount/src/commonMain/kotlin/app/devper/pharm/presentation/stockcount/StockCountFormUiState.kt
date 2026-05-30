package app.devper.pharm.presentation.stockcount

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.parser.StockCountInputBuilder
import app.devper.pharm.domain.util.DrugSearch
import app.devper.pharm.ui.common.BaseFormUiState

data class StockCountFormUiState(
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    val drugs: List<Drug> = emptyList(),
    val counts: Map<String, String> = emptyMap(),
    val note: String = "",
    val query: String = "",
    val showSubmitConfirm: Boolean = false,
    override val error: String? = null,
) : BaseFormUiState<StockCountFormUiState> {
    private val drugById: Map<String, Drug> = drugs.associateBy { it.id }

    val filtered: List<Drug> = DrugSearch.filter(drugs, query)

    val pendingLines: List<Pair<String, Int>> = StockCountInputBuilder.parsePending(counts)

    val changedLines: List<Pair<String, Int>> = pendingLines.filter { (id, counted) ->
        val drug = drugById[id] ?: return@filter false
        counted != drug.stock
    }

    val changedCount: Int = changedLines.size

    val totalAbsDelta: Int = pendingLines.sumOf { (id, counted) ->
        val drug = drugById[id] ?: return@sumOf 0
        if (counted == drug.stock) 0 else kotlin.math.abs(counted - drug.stock)
    }

    val topDiscrepancies: List<StockCountDiscrepancy> = changedLines
        .mapNotNull { (id, counted) ->
            val drug = drugById[id] ?: return@mapNotNull null
            StockCountDiscrepancy(
                drugId = id,
                drugName = drug.name,
                unit = drug.unit.orEmpty(),
                systemStock = drug.stock,
                counted = counted,
                delta = counted - drug.stock,
            )
        }
        .sortedByDescending { kotlin.math.abs(it.delta) }
        .take(5)

    override val canSubmit: Boolean = !saving && !loading && pendingLines.isNotEmpty()

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override fun withError(error: String?) = copy(error = error)
}

data class StockCountDiscrepancy(
    val drugId: String,
    val drugName: String,
    val unit: String,
    val systemStock: Int,
    val counted: Int,
    val delta: Int,
)
