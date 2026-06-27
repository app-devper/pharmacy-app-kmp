package app.devper.pharm.common.error

sealed class CommonUiStateMessage {
    data object ExportEmpty : CommonUiStateMessage()
    data object Saved : CommonUiStateMessage()
    data class ExportDone(val path: String) : CommonUiStateMessage()
}
