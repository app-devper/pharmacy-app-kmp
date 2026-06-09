package app.devper.pharm.presentation.help

import app.devper.pharm.presentation.help.exception.HelpException
import app.devper.pharm.ui.common.BaseUiState

data class HelpUiState(
    override val loading: Boolean = false,
    val markdown: String = "",
    val errorTyped: HelpException? = null,
) : BaseUiState {
    override val domainError: HelpException? get() = errorTyped
}
