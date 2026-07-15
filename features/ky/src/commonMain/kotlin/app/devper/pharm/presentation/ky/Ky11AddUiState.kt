package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.validation.isKy11DraftValid
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.BaseFormUiState

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
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    override val loading: Boolean = false,
    val errorState: AppException? = null,
) : BaseFormUiState<Ky11AddUiState> {

    override val canSubmit: Boolean
        get() = !saving && isKy11DraftValid(draft.date, draft.drugName, draft.unit, draft.qty)

    override val hasUnsavedChanges: Boolean
        get() = draft != Ky11Draft()

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
