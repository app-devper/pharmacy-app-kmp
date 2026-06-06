package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.parser.Ky11DraftBuilder
import app.devper.pharm.ui.common.BaseUiState

data class Ky11Draft(
    val date: String = "",
    val drugName: String = "",
    val regNo: String = "",
    val qty: String = "",
    val unit: String = "",
    val buyerName: String = "",
    val purpose: String = "",
    val pharmacist: String = "",
)

data class Ky11AddUiState(
    val draft: Ky11Draft = Ky11Draft(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    override val loading: Boolean = false,
    override val error: String? = null,
) : BaseUiState {
    val canSubmitDraft: Boolean
        get() = !saving && Ky11DraftBuilder.isDraftValid(draft.date, draft.drugName, draft.unit, draft.qty)
}
