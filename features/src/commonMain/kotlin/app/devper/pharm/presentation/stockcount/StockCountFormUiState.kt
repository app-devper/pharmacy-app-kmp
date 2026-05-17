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
    override val error: String? = null,
) : BaseFormUiState<StockCountFormUiState> {
    val filtered: List<Drug> get() = DrugSearch.filter(drugs, query)

    val pendingLines: List<Pair<String, Int>>
        get() = StockCountInputBuilder.parsePending(counts)

    val changedLines: List<Pair<String, Int>>
        get() = pendingLines.filter { (id, counted) ->
            val drug = drugs.firstOrNull { it.id == id } ?: return@filter false
            counted != drug.stock
        }

    val changedCount: Int get() = changedLines.size

    val totalAbsDelta: Int
        get() = changedLines.sumOf { (id, counted) ->
            val drug = drugs.firstOrNull { it.id == id } ?: return@sumOf 0
            kotlin.math.abs(counted - drug.stock)
        }

    override val canSubmit: Boolean
        get() = !saving && !loading && pendingLines.isNotEmpty()

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override fun withError(error: String?) = copy(error = error)
}
