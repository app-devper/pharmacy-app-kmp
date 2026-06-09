package app.devper.pharm.presentation.help

import app.devper.pharm.presentation.help.exception.HelpException
import app.devper.pharm.ui.common.BaseViewModel

class HelpViewModel(
    private val loader: MarkdownLoader,
) : BaseViewModel<HelpUiState>(HelpUiState()) {

    init { reload() }

    fun dismissError() = setState { copy(errorTyped = null) }

    fun reload() {
        launchResult(
            block = { runCatching { loader.loadUserGuide() } },
            onSuccess = { md -> setState { copy(loading = false, markdown = md) } },
            onFailure = { e -> setState { copy(loading = false, errorTyped = HelpException.Markdown.LoadFailed(e)) } },
            withLoading = { l -> setState { copy(loading = l) } },
        )
    }
}
