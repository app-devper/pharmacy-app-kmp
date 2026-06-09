package app.devper.pharm.common.error

sealed class CommonUiStateMessage {
    data object ExportEmpty : CommonUiStateMessage()
}
