package app.devper.pharm.presentation.help

import app.devper.pharm.features.help.resources.Res
import app.devper.pharm.ui.common.BaseViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi

class HelpViewModel : BaseViewModel<HelpUiState>(HelpUiState()) {

    init { reload() }

    fun dismissError() = setState { copy(error = null) }

    fun reload() {
        launchResult(
            block = {
                runCatching {
                    @OptIn(ExperimentalResourceApi::class)
                    Res.readBytes("files/user_guide.md").decodeToString()
                }
            },
            onSuccess = { md -> setState { copy(loading = false, markdown = md) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดคู่มือไม่สำเร็จ") } },
            withLoading = { l -> setState { copy(loading = l) } },
        )
    }
}
