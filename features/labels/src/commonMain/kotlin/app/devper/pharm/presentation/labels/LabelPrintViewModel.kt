package app.devper.pharm.presentation.labels

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.LabelLine
import app.devper.pharm.domain.model.LabelSize
import app.devper.pharm.domain.param.labels.PrintLabelsParam
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.usecase.inventory.GetDrugsUseCase
import app.devper.pharm.domain.usecase.inventory.PrintLabelsUseCase
import app.devper.pharm.presentation.labels.exception.LabelPrintUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel

class LabelPrintViewModel(
    private val getDrugs: GetDrugsUseCase,
    private val printLabels: PrintLabelsUseCase,
) : BaseLoadableViewModel<LabelPrintUiState>(LabelPrintUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun onAddDrug(drug: Drug) {
        setState {
            val idx = lines.indexOfFirst { it.drugId == drug.id }
            val next = if (idx >= 0) {
                lines.toMutableList().apply {
                    val current = this[idx]
                    this[idx] = current.copy(copies = current.copies + 1)
                }
            } else {
                lines + drug.toLine()
            }
            copy(lines = next)
        }
    }

    fun onRemoveLine(index: Int) {
        setState {
            if (index !in lines.indices) return@setState this
            copy(lines = lines.toMutableList().apply { removeAt(index) })
        }
    }

    fun onChangeCopies(index: Int, copies: Int) {
        setState {
            if (index !in lines.indices) return@setState this
            val capped = copies.coerceIn(0, MAX_COPIES_PER_LINE)
            copy(lines = lines.replaceAt(index) { it.copy(copies = capped) })
        }
    }

    fun onChangeBarcode(index: Int, barcode: String) {
        setState {
            if (index !in lines.indices) return@setState this
            copy(lines = lines.replaceAt(index) { it.copy(barcode = barcode) })
        }
    }

    fun onToggleIncludePrice(index: Int, include: Boolean) {
        setState {
            if (index !in lines.indices) return@setState this
            copy(lines = lines.replaceAt(index) { it.copy(includePrice = include) })
        }
    }

    fun onSizeChange(size: LabelSize) = setState { copy(size = size) }

    fun onClearAll() = setState { copy(lines = emptyList()) }

    fun onPrint() {
        val s = current
        if (!s.canPrint) return
        setState { copy(printing = true) }
        launchResult(
            block = {
                printLabels(
                    PrintLabelsParam(
                        size = s.size,
                        lines = s.lines.filter { it.copies > 0 },
                    ),
                )
            },
            onSuccess = { feedback -> setState { copy(printing = false, message = feedback) } },
            onFailure = { e -> setState { copy(printing = false, errorState = LabelPrintUiStateError.PrintFailed(e)) } },
        )
    }

    fun dismissMessage() = setState { copy(message = null) }

    fun reload() {
        setState { copy(loading = drugs.isEmpty(), errorState = null) }
        launchResult(
            block = { getDrugs() },
            onSuccess = { list -> setState { copy(loading = false, drugs = list) } },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
        )
    }

    private fun Drug.toLine(): LabelLine = LabelLine(
        drugId = id,
        drugName = name,
        lotNumber = "",
        barcode = (barcode?.takeIf { it.isNotBlank() }) ?: id,
        price = sellPrice.amount,
        includePrice = true,
        copies = 1,
    )

    private fun List<LabelLine>.replaceAt(index: Int, transform: (LabelLine) -> LabelLine): List<LabelLine> =
        toMutableList().apply { this[index] = transform(this[index]) }

    private companion object {
        const val MAX_COPIES_PER_LINE = 500
    }
}
