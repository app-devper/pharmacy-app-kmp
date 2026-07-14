package app.devper.pharm.presentation.help

data class HelpCallbacks(
    val onDismissError: () -> Unit = {},
) {
    companion object {
        val Preview = HelpCallbacks()
    }
}
