package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.extension.buildKy9Draft
import app.devper.pharm.domain.extension.isKy9DraftValid
import app.devper.pharm.ui.common.BaseUiState

data class Ky9AddUiState(
    val draft: Ky9Draft = Ky9Draft(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    override val loading: Boolean = false,
    override val error: String? = null,
) : BaseUiState {
    val canSubmitDraft: Boolean
        get() = !saving && isKy9DraftValid(
            date = draft.date,
            drugName = draft.drugName,
            unit = draft.unit,
            qty = draft.qty,
            pricePerUnit = draft.pricePerUnit,
        )
}
