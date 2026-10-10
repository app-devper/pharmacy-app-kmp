package app.devper.pharm.presentation.stockcount

import kotlinx.datetime.LocalDate
import app.devper.pharm.domain.model.LotChoice
import app.devper.pharm.domain.model.LotTarget
import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.validation.buildStockCountInput
import app.devper.pharm.domain.validation.parsePendingStockCounts
import app.devper.pharm.domain.extension.searchByQuery
import app.devper.pharm.common.AppException
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
    val lotStep: List<CountLotLine>? = null,
    val chosenLots: Map<String, LotTarget> = emptyMap(),
    val pendingDraftAction: StockCountDraftAction? = null,
    val errorState: AppException? = null,
) : BaseFormUiState<StockCountFormUiState> {
    private val drugById: Map<String, Drug> by lazy { drugs.associateBy { it.id } }

    val filtered: List<Drug> by lazy { drugs.searchByQuery(query) }

    val pendingLines: List<Pair<String, Int>> by lazy { parsePendingStockCounts(counts) }

    val invalidCountIds: Set<String> by lazy {
        counts.mapNotNullTo(mutableSetOf()) { (id, raw) ->
            val parsed = raw.toIntOrNull()
            id.takeIf { raw.isNotBlank() && (parsed == null || parsed < 0) }
        }
    }

    val changedLines: List<Pair<String, Int>> by lazy {
        pendingLines.filter { (id, counted) ->
            val drug = drugById[id] ?: return@filter false
            counted != drug.stock.value
        }
    }

    val changedCount: Int get() = changedLines.size

    val totalAbsDelta: Int by lazy {
        pendingLines.sumOf { (id, counted) ->
            val drug = drugById[id] ?: return@sumOf 0
            val systemStock = drug.stock.value
            if (counted == systemStock) 0 else kotlin.math.abs(counted - systemStock)
        }
    }

    val topDiscrepancies: List<StockCountDiscrepancy> by lazy {
        changedLines
            .mapNotNull { (id, counted) ->
                val drug = drugById[id] ?: return@mapNotNull null
                val systemStock = drug.stock.value
                StockCountDiscrepancy(
                    drugId = id,
                    drugName = drug.name,
                    unit = drug.unit.orEmpty(),
                    systemStock = systemStock,
                    counted = counted,
                    delta = counted - systemStock,
                )
            }
            .sortedByDescending { kotlin.math.abs(it.delta) }
            .take(5)
    }

    override val canSubmit: Boolean
        get() = !saving && !loading && pendingLines.isNotEmpty() && invalidCountIds.isEmpty()

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}

enum class StockCountDraftAction {
    FillFromSystem,
    ClearDraft,
}

data class StockCountDiscrepancy(
    val drugId: String,
    val drugName: String,
    val unit: String,
    val systemStock: Int,
    val counted: Int,
    val delta: Int,
)

data class CountLotLine(
    val drugId: String,
    val drugName: String,
    val delta: Int,
    val lot: LotChoice,
) {
    fun target(): LotTarget? = lot.target
}
