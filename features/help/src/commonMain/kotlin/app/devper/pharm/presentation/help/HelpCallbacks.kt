package app.devper.pharm.presentation.help

data class HelpCallbacks(
    val onReload: () -> Unit = {},
    val onDismissError: () -> Unit = {},
) {
    companion object {
        val Preview = HelpCallbacks()
    }
}
