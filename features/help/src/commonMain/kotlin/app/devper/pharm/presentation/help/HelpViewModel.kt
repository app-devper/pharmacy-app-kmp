package app.devper.pharm.presentation.help

import app.devper.pharm.presentation.help.exception.HelpUiStateError
import app.devper.pharm.ui.common.BaseViewModel

class HelpViewModel(
    private val loader: MarkdownLoader,
) : BaseViewModel<HelpUiState>(HelpUiState()) {

    init { reload() }

    fun dismissError() = setState { copy(errorState = null) }

    fun reload() {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { runCatching { loader.loadUserGuide() } },
            onSuccess = { md -> setState { copy(loading = false, markdown = md) } },
            onFailure = { e -> setState { copy(loading = false, errorState = HelpUiStateError.LoadFailed(e)) } },
        )
    }
}
