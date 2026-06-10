package app.devper.pharm.ui.common

import app.devper.pharm.common.AppException

interface BaseUiState {
    val loading: Boolean
    val domainError: AppException? get() = null
}
