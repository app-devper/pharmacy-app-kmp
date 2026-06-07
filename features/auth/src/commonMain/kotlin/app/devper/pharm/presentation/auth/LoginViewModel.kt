package app.devper.pharm.presentation.auth

import androidx.lifecycle.viewModelScope
import app.devper.pharm.common.userMessageOr
import app.devper.pharm.domain.model.LocalePreference
import app.devper.pharm.domain.observer.UiPreferencesProvider
import app.devper.pharm.domain.usecase.LoginUseCase
import app.devper.pharm.domain.usecase.SetLocalePreferenceUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private const val LOGIN_REQUIRED_FIELDS = "กรุณากรอกชื่อผู้ใช้และรหัสผ่าน"
private const val LOGIN_FAILED = "เข้าสู่ระบบไม่สำเร็จ"

class LoginViewModel(
    private val login: LoginUseCase,
    uiPreferences: UiPreferencesProvider,
    private val setLocale: SetLocalePreferenceUseCase,
) : BaseLoadableViewModel<LoginUiState>(LoginUiState()) {

    init {
        uiPreferences.state
            .onEach { prefs -> setState { copy(locale = prefs.locale.wire) } }
            .launchIn(viewModelScope)
    }

    fun onUsernameChange(value: String) = setState { copy(username = value, error = null) }
    fun onPasswordChange(value: String) = setState { copy(password = value, error = null) }

    fun onLocaleChange(value: String) {
        val parsed = LocalePreference.parse(value)
        if (parsed.wire == current.locale) return
        setLocale(parsed)
    }

    fun submit() {
        val s = current
        if (s.username.isBlank() || s.password.isBlank()) {
            setState { copy(error = LOGIN_REQUIRED_FIELDS) }
            return
        }
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { login(s.username, s.password) },
            onSuccess = { user -> setState { copy(loading = false, loggedInUser = user, password = "") } },
            onFailure = { e ->
                setState { copy(loading = false, error = e.userMessageOr(LOGIN_FAILED), password = "") }
            },
        )
    }
}
