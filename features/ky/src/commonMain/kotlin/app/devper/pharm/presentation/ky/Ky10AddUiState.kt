package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.extension.isKy10DraftValid
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.BaseFormUiState

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
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    override val loading: Boolean = false,
    val errorState: AppException? = null,
) : BaseFormUiState<Ky10AddUiState> {

    override val canSubmit: Boolean
        get() = !saving && isKy10DraftValid(draft.date, draft.drugName, draft.unit, draft.qty)

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
