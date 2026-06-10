package app.devper.pharm.ui.common

import app.devper.pharm.common.AppException

interface LoadableUiState<S : LoadableUiState<S>> : BaseUiState {
    fun withLoading(value: Boolean): S
    fun withDomainError(error: AppException?): S
}
