package app.devper.pharm.presentation.help

import app.devper.pharm.ui.common.BaseViewModel

class HelpViewModel(
    private val loader: MarkdownLoader,
) : BaseViewModel<HelpUiState>(HelpUiState()) {

    init { reload() }

    fun dismissError() = setState { copy(error = null) }

    fun reload() {
        launchResult(
            block = { runCatching { loader.loadUserGuide() } },
            onSuccess = { md -> setState { copy(loading = false, markdown = md) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดคู่มือไม่สำเร็จ") } },
            withLoading = { l -> setState { copy(loading = l) } },
        )
    }
}
