package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.parser.Ky9DraftBuilder
import app.devper.pharm.ui.common.BaseUiState

data class Ky9AddUiState(
    val draft: Ky9Draft = Ky9Draft(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    override val loading: Boolean = false,
    override val error: String? = null,
) : BaseUiState {
    val canSubmitDraft: Boolean
        get() = !saving && Ky9DraftBuilder.isDraftValid(
            date = draft.date,
            drugName = draft.drugName,
            unit = draft.unit,
            qty = draft.qty,
            pricePerUnit = draft.pricePerUnit,
        )
}
