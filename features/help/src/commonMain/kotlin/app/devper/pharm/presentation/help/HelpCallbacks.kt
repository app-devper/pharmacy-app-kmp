package app.devper.pharm.presentation.help

data class HelpCallbacks(
    val onDismissError: () -> Unit = {},
    val onTocClick: (String) -> Unit = {},
) {
    companion object {
        val Preview = HelpCallbacks()
    }
}
