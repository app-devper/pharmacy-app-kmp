package app.devper.pharm.presentation.help

import app.devper.pharm.ui.common.BaseUiState

data class HelpUiState(
    override val loading: Boolean = false,
    val markdown: String = "",
    override val error: String? = null,
) : BaseUiState
