package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.extension.buildKy10Draft
import app.devper.pharm.domain.extension.isKy10DraftValid
import app.devper.pharm.ui.common.BaseUiState

data class Ky10Draft(
    val date: String = "",
    val drugName: String = "",
    val regNo: String = "",
    val qty: String = "",
    val unit: String = "",
    val buyerName: String = "",
    val buyerAddress: String = "",
    val rxNo: String = "",
    val doctor: String = "",
    val balance: String = "",
)

data class Ky10AddUiState(
    val draft: Ky10Draft = Ky10Draft(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    override val loading: Boolean = false,
    override val error: String? = null,
) : BaseUiState {
    val canSubmitDraft: Boolean
        get() = !saving && isKy10DraftValid(draft.date, draft.drugName, draft.unit, draft.qty)
}
