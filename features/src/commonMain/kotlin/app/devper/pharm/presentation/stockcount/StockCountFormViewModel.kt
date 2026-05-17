package app.devper.pharm.presentation.stockcount

import app.devper.pharm.domain.param.CreateStockCountParam
import app.devper.pharm.domain.parser.StockCountInputBuilder
import app.devper.pharm.domain.usecase.CreateStockCountUseCase
import app.devper.pharm.domain.usecase.GetDrugsUseCase
import app.devper.pharm.ui.common.BaseFormViewModel

class StockCountFormViewModel(
    private val getDrugs: GetDrugsUseCase,
    private val createStockCount: CreateStockCountUseCase,
) : BaseFormViewModel<StockCountFormUiState>(StockCountFormUiState()) {

    init { reload() }

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

    fun reload() {
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getDrugs() },
            onSuccess = { list -> setState { copy(loading = false, drugs = list) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดยาไม่สำเร็จ") } },
        )
    }

    override suspend fun persist(): Result<Unit> {
        val s = current
        val lines = StockCountInputBuilder.build(s.counts)
        return createStockCount(CreateStockCountParam(note = s.note.trim(), items = lines)).map { Unit }
    }
}
