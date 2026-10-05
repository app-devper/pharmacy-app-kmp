package app.devper.pharm.presentation.stockcount

import app.devper.pharm.domain.usecase.inventory.ListLotsUseCase
import app.devper.pharm.presentation.stockcount.exception.StockCountUiStateError

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.model.StockCountDraft
import app.devper.pharm.domain.param.inventory.CreateStockCountParam
import app.devper.pharm.domain.validation.buildStockCountInput
import app.devper.pharm.domain.validation.parsePendingStockCounts
import app.devper.pharm.domain.usecase.inventory.ClearStockCountDraftUseCase
import app.devper.pharm.domain.usecase.inventory.CreateStockCountUseCase
import app.devper.pharm.domain.usecase.inventory.GetDrugsUseCase
import app.devper.pharm.domain.usecase.inventory.LoadStockCountDraftUseCase
import app.devper.pharm.domain.usecase.inventory.SaveStockCountDraftUseCase
import app.devper.pharm.ui.common.BaseFormViewModel
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@OptIn(FlowPreview::class, ExperimentalTime::class)
class StockCountFormViewModel(
    private val getDrugs: GetDrugsUseCase,
    private val createStockCount: CreateStockCountUseCase,
    private val loadDraft: LoadStockCountDraftUseCase,
    private val saveDraft: SaveStockCountDraftUseCase,
    private val clearDraft: ClearStockCountDraftUseCase,
    private val listLots: ListLotsUseCase,
) : BaseFormViewModel<StockCountFormUiState>(StockCountFormUiState()) {

    init {
        hydrate()
        state
            .map { DraftSnapshot(it.counts, it.note) }
            .drop(1)
            .distinctUntilChanged()
            .debounce(DRAFT_DEBOUNCE_MS.milliseconds)
            .onEach { snapshot ->
                if (snapshot.counts.isEmpty() && snapshot.note.isBlank()) {
                    clearDraft(Unit)
                } else {
                    saveDraft(
                        StockCountDraft(
                            counts = snapshot.counts,
                            note = snapshot.note,
                            updatedAt = Clock.System.now().toEpochMilliseconds(),
                        )
                    )
                }
            }
            .launchIn(viewModelScope)
        reload()
    }

    fun onQueryChange(value: String) = setState { copy(query = value) }
    fun onNoteChange(value: String) = setState { copy(note = value) }

    fun onCountChange(drugId: String, value: String) {
        val cleaned = value.filter { it.isDigit() }
        setState {
            val next = if (cleaned.isEmpty()) counts - drugId
            else counts + (drugId to cleaned)
            copy(counts = next)
        }
    }

    fun requestFillFromSystem() {
        if (current.counts.isEmpty()) fillFromSystem()
        else setState { copy(pendingDraftAction = StockCountDraftAction.FillFromSystem) }
    }

    fun requestClearDraft() {
        if (current.counts.isNotEmpty() || current.note.isNotBlank()) {
            setState { copy(pendingDraftAction = StockCountDraftAction.ClearDraft) }
        }
    }

    fun confirmDraftAction() {
        val action = current.pendingDraftAction ?: return
        setState { copy(pendingDraftAction = null) }
        when (action) {
            StockCountDraftAction.FillFromSystem -> fillFromSystem()
            StockCountDraftAction.ClearDraft -> clearDraft()
        }
    }

    fun cancelDraftAction() = setState { copy(pendingDraftAction = null) }

    fun requestSubmit() {
        if (!current.canSubmit) return
        setState { copy(showSubmitConfirm = true) }
    }

    fun cancelSubmit() = setState { copy(showSubmitConfirm = false) }

    fun confirmSubmit() {
        setState { copy(showSubmitConfirm = false, chosenLots = emptyMap()) }
        val s = current
        val byId = s.drugs.associateBy { it.id }
        val increases = s.changedLines.mapNotNull { (id, counted) ->
            val drug = byId[id] ?: return@mapNotNull null
            (counted - drug.stock.value).takeIf { it > 0 }?.let { Triple(id, drug.name, it) }
        }
        if (increases.isEmpty()) {
            submit()
            return
        }
        // Counted increases of lot-tracked drugs must name their lot (ADR-0007).
        setState { copy(saving = true) }
        launchResult(
            block = {
                var failure: Throwable? = null
                val lines = increases.mapNotNull { (id, name, delta) ->
                    if (failure != null) return@mapNotNull null
                    listLots(id).fold(
                        onSuccess = { all ->
                            val lots = all.filterNot { it.writtenOff }.sortedByDescending { it.expiryDate }
                            if (lots.isEmpty()) null
                            else CountLotLine(drugId = id, drugName = name, delta = delta, lots = lots, choice = lots.first().id)
                        },
                        onFailure = { e -> failure = e; null },
                    )
                }
                failure?.let { Result.failure(it) } ?: Result.success(lines)
            },
            onSuccess = { lines ->
                setState { copy(saving = false) }
                if (lines.isEmpty()) submit() else setState { copy(lotStep = lines) }
            },
            onFailure = { e -> setState { copy(saving = false, errorState = StockCountUiStateError.LoadDrugsFailed(e)) } },
        )
    }

    fun onLotChoice(drugId: String, choice: String) = patchLotLine(drugId) { copy(choice = choice) }
    fun onNewLotNumber(drugId: String, value: String) = patchLotLine(drugId) { copy(newLotNumber = value) }
    fun onNewLotExpiry(drugId: String, value: String) = patchLotLine(drugId) { copy(newLotExpiry = value) }
    fun cancelLotStep() = setState { copy(lotStep = null) }

    fun confirmLotStep() {
        val lines = current.lotStep ?: return
        val targets = lines.associate { it.drugId to it.target() }
        if (targets.values.any { it == null }) return
        setState { copy(lotStep = null, chosenLots = targets.mapValues { it.value!! }) }
        submit()
    }

    private fun patchLotLine(drugId: String, transform: CountLotLine.() -> CountLotLine) = setState {
        copy(lotStep = lotStep?.map { if (it.drugId == drugId) it.transform() else it })
    }

    fun reload() {
        setState { copy(loading = drugs.isEmpty(), errorState = null) }
        launchResult(
            block = { getDrugs() },
            onSuccess = { list ->
                setState {
                    val validIds = list.asSequence().map { it.id }.toHashSet()
                    val pruned = counts.filterKeys { it in validIds }
                    copy(loading = false, drugs = list, counts = pruned)
                }
            },
            onFailure = { e -> setState { copy(loading = false, errorState = StockCountUiStateError.LoadDrugsFailed(e)) } },
        )
    }

    override suspend fun persist(): Result<Unit> {
        val s = current
        val lines = buildStockCountInput(s.counts).map { it.copy(lot = s.chosenLots[it.drugId]) }
        val result = createStockCount(CreateStockCountParam(note = s.note.trim(), items = lines)).map { Unit }
        if (result.isSuccess) {
            clearDraft(Unit)
            setState { copy(counts = emptyMap(), note = "", chosenLots = emptyMap()) }
        }
        return result
    }

    private fun hydrate() {
        val draft = loadDraft(Unit).getOrNull() ?: return
        if (draft.isEmpty) return
        setState { copy(counts = draft.counts, note = draft.note) }
    }

    private fun fillFromSystem() = setState {
        copy(counts = drugs.associate { it.id to it.stock.value.coerceAtLeast(0).toString() })
    }

    private fun clearDraft() = setState { copy(counts = emptyMap(), note = "") }

    private data class DraftSnapshot(val counts: Map<String, String>, val note: String)

    companion object {
        private const val DRAFT_DEBOUNCE_MS = 500L
    }
}
