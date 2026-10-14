package app.devper.pharm.ui.common

import app.devper.pharm.common.error.CommonUiStateMessage

interface ExportableUiState<S : ExportableUiState<S>> : LoadableUiState<S> {
    fun withExporting(exporting: Boolean): S
    fun withMessage(message: CommonUiStateMessage?): S
}
