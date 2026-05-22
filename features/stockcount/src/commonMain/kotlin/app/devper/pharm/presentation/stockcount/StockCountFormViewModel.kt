package app.devper.pharm.presentation.stockcount

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.model.StockCountDraft
import app.devper.pharm.domain.param.CreateStockCountParam
import app.devper.pharm.domain.parser.StockCountInputBuilder
import app.devper.pharm.domain.repository.StockCountDraftRepository
import app.devper.pharm.domain.usecase.CreateStockCountUseCase
import app.devper.pharm.domain.usecase.GetDrugsUseCase
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
    private val draftRepo: StockCountDraftRepository,
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
                    draftRepo.clear()
                } else {
                    draftRepo.save(
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

    fun onFillFromSystem() = setState {
        copy(counts = drugs.associate { it.id to it.stock.coerceAtLeast(0).toString() })
    }

    fun onClear() = setState { copy(counts = emptyMap()) }

    fun onClearDraft() = setState { copy(counts = emptyMap(), note = "") }

    fun requestSubmit() {
        if (!current.canSubmit) return
        setState { copy(showSubmitConfirm = true) }
    }

    fun cancelSubmit() = setState { copy(showSubmitConfirm = false) }

    fun confirmSubmit() {
        setState { copy(showSubmitConfirm = false) }
        submit()
    }

    fun reload() {
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getDrugs() },
            onSuccess = { list ->
                setState {
                    val validIds = list.asSequence().map { it.id }.toHashSet()
                    val pruned = counts.filterKeys { it in validIds }
                    copy(loading = false, drugs = list, counts = pruned)
                }
            },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดยาไม่สำเร็จ") } },
        )
    }

    override suspend fun persist(): Result<Unit> {
        val s = current
        val lines = StockCountInputBuilder.build(s.counts)
        val result = createStockCount(CreateStockCountParam(note = s.note.trim(), items = lines)).map { Unit }
        if (result.isSuccess) {
            draftRepo.clear()
            setState { copy(counts = emptyMap(), note = "") }
        }
        return result
    }

    private fun hydrate() {
        val draft = draftRepo.load()
        if (draft.isEmpty) return
        setState { copy(counts = draft.counts, note = draft.note) }
    }

    private data class DraftSnapshot(val counts: Map<String, String>, val note: String)

    companion object {
        private const val DRAFT_DEBOUNCE_MS = 500L
    }
}
