package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.extension.buildKy12Draft
import app.devper.pharm.domain.extension.isKy12DraftValid
import app.devper.pharm.ui.common.BaseUiState

data class Ky12Draft(
    val date: String = "",
    val drugName: String = "",
    val regNo: String = "",
    val qty: String = "",
    val unit: String = "",
    val rxNo: String = "",
    val patientName: String = "",
    val doctor: String = "",
    val hospital: String = "",
    val totalValue: String = "",
    val status: String = "จ่ายแล้ว",
)

data class Ky12AddUiState(
    val draft: Ky12Draft = Ky12Draft(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    override val loading: Boolean = false,
    override val error: String? = null,
) : BaseUiState {
    val canSubmitDraft: Boolean
        get() = !saving && isKy12DraftValid(draft.date, draft.drugName, draft.unit, draft.qty)
}
