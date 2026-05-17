package app.devper.pharm.presentation.users

data class UserFormCallbacks(
    val onFirstName: (String) -> Unit,
    val onLastName: (String) -> Unit,
    val onUsername: (String) -> Unit,
    val onPassword: (String) -> Unit,
    val onPhone: (String) -> Unit,
    val onEmail: (String) -> Unit,
    val onSubmit: () -> Unit,
    val onBack: () -> Unit,
    val onDismissError: () -> Unit,
) {
    companion object {
        val Preview = UserFormCallbacks(
            onFirstName = {}, onLastName = {}, onUsername = {}, onPassword = {},
            onPhone = {}, onEmail = {},
            onSubmit = {}, onBack = {}, onDismissError = {},
        )
    }
}
