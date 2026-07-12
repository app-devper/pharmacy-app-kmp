package app.devper.pharm.presentation.settings.exception

import app.devper.pharm.common.AppException

sealed class SettingsUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadSettingsFailed(cause: Throwable? = null) : SettingsUiStateError("settings.load_failed", cause)
    class TestPrintFailed(cause: Throwable? = null) : SettingsUiStateError("settings.test_print_failed", cause)
}
