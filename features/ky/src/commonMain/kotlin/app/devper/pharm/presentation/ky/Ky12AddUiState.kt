package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.validation.isKy12DraftValid
import app.devper.pharm.domain.validation.Check
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.BaseFormUiState

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
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    override val loading: Boolean = false,
    val errorState: AppException? = null,
) : BaseFormUiState<Ky12AddUiState> {

    override val canSubmit: Boolean
        get() = !saving &&
            isKy12DraftValid(draft.date, draft.drugName, draft.unit, draft.qty) &&
            (draft.totalValue.isBlank() || Check.nonNegativeDouble(draft.totalValue))

    override val hasUnsavedChanges: Boolean
        get() = draft != Ky12Draft()

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
