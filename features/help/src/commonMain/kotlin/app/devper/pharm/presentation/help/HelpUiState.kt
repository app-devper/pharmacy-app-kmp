package app.devper.pharm.presentation.help

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.help.exception.HelpUiStateError
import app.devper.pharm.ui.common.BaseUiState

data class HelpUiState(
    override val loading: Boolean = false,
    val markdown: String = "",
    val errorState: HelpUiStateError? = null,
) : BaseUiState {
    override val domainError: HelpUiStateError? get() = errorState
}
