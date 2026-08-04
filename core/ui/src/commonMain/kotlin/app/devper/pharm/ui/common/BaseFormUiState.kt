package app.devper.pharm.ui.common

import app.devper.pharm.common.AppException

interface BaseFormUiState<S : BaseFormUiState<S>> : BaseUiState {
    val saving: Boolean
    val saved: Boolean
    val canSubmit: Boolean
    val hasUnsavedChanges: Boolean
        get() = false

    fun withSaving(saving: Boolean): S
    fun withSaved(saved: Boolean): S
    fun withDomainError(error: AppException?): S
}
