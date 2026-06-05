package app.devper.pharm.presentation.profile

data class ProfileCallbacks(
    val onFirstName: (String) -> Unit = {},
    val onLastName: (String) -> Unit = {},
    val onPhone: (String) -> Unit = {},
    val onEmail: (String) -> Unit = {},
    val onSubmit: () -> Unit = {},
    val onDismissError: () -> Unit = {},
    val onOpenPasswordPanel: () -> Unit = {},
    val onClosePasswordPanel: () -> Unit = {},
    val onOldPassword: (String) -> Unit = {},
    val onNewPassword: (String) -> Unit = {},
    val onConfirmPassword: (String) -> Unit = {},
    val onSubmitPasswordChange: () -> Unit = {},
    val onDismissPasswordError: () -> Unit = {},
    val onThemeChange: (String) -> Unit = {},
    val onFontSizeChange: (String) -> Unit = {},
    val onDensityChange: (String) -> Unit = {},
    val onBack: () -> Unit = {},
) {
    companion object {
        val Preview = ProfileCallbacks()
    }
}
