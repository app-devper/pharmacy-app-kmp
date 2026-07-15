package app.devper.pharm.presentation.auth

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.model.LocalePreference
import app.devper.pharm.domain.observer.UiPreferencesProvider
import app.devper.pharm.domain.usecase.auth.LoginUseCase
import app.devper.pharm.domain.usecase.settings.SetLastUsernameUseCase
import app.devper.pharm.domain.usecase.settings.SetLocalePreferenceUseCase
import app.devper.pharm.presentation.auth.exception.LoginUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginViewModel(
    private val login: LoginUseCase,
    uiPreferences: UiPreferencesProvider,
    private val setLocale: SetLocalePreferenceUseCase,
    private val setLastUsername: SetLastUsernameUseCase,
) : BaseLoadableViewModel<LoginUiState>(LoginUiState()) {

    init {
        uiPreferences.state
            .onEach { prefs ->
                setState {
                    if (username.isBlank() && prefs.lastUsername.isNotBlank()) copy(locale = prefs.locale.wire, username = prefs.lastUsername)
                    else copy(locale = prefs.locale.wire)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onUsernameChange(value: String) = setState { copy(username = value, errorState = null) }
    fun onPasswordChange(value: String) = setState { copy(password = value, errorState = null) }

    fun onLocaleChange(value: String) {
        val parsed = LocalePreference.parse(value)
        if (parsed.wire == current.locale) return
        setLocale(parsed)
    }

    fun submit() {
        val s = current
        if (s.loading) return
        if (s.username.isBlank() || s.password.isBlank()) {
            setState { copy(validationRequested = true, errorState = LoginUiStateError.RequiredFields()) }
            return
        }
        setState { copy(loading = true, validationRequested = false, errorState = null) }
        launchResult(
            block = { login(s.username, s.password) },
            onSuccess = { user ->
                setLastUsername(s.username)
                setState { copy(loading = false, loggedInUser = user, password = "") }
            },
            onFailure = { e ->
                setState { copy(loading = false, errorState = LoginUiStateError.LoginFailed(e), password = "") }
            },
        )
    }
}
