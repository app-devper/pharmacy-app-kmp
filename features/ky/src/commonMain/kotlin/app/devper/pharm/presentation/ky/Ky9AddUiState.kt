package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.validation.isKy9DraftValid
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.BaseFormUiState

data class Ky9AddUiState(
    val draft: Ky9Draft = Ky9Draft(),
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    override val loading: Boolean = false,
    val errorState: AppException? = null,
) : BaseFormUiState<Ky9AddUiState> {

    override val canSubmit: Boolean
        get() = !saving && isKy9DraftValid(
            date = draft.date,
            drugName = draft.drugName,
            unit = draft.unit,
            qty = draft.qty,
            pricePerUnit = draft.pricePerUnit,
        )

    override val hasUnsavedChanges: Boolean
        get() = draft != Ky9Draft()

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
