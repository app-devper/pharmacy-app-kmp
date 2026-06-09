package app.devper.pharm.ui.common

import app.devper.pharm.common.AppException

interface BaseFormUiState<S : BaseFormUiState<S>> : BaseUiState {
    val saving: Boolean
    val saved: Boolean
    val canSubmit: Boolean

    fun withSaving(saving: Boolean): S
    fun withSaved(saved: Boolean): S
    fun withError(error: String?): S

    @Suppress("UNCHECKED_CAST")
    fun withDomainError(error: AppException?): S = this as S
}
