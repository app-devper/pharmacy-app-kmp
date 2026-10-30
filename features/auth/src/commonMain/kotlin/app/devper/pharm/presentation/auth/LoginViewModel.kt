package app.devper.pharm.presentation.auth

import androidx.lifecycle.viewModelScope
import app.devper.pharm.common.platform.PasskeyAuthenticator
import app.devper.pharm.domain.model.LocalePreference
import app.devper.pharm.domain.model.User
import app.devper.pharm.domain.observer.SessionExpiryProvider
import app.devper.pharm.domain.observer.UiPreferencesProvider
import app.devper.pharm.domain.repository.settings.UiPreferencesRepository
import app.devper.pharm.domain.usecase.auth.ContinueFromUmUseCase
import app.devper.pharm.domain.usecase.auth.LoginUseCase
import app.devper.pharm.domain.usecase.auth.LoginWithPasskeyUseCase
import app.devper.pharm.presentation.auth.exception.LoginUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginViewModel(
    private val login: LoginUseCase,
    private val loginWithPasskey: LoginWithPasskeyUseCase,
    private val continueFromUm: ContinueFromUmUseCase,
    authenticator: PasskeyAuthenticator,
    uiPreferences: UiPreferencesProvider,
    private val preferences: UiPreferencesRepository,
    private val sessionExpiry: SessionExpiryProvider,
) : BaseLoadableViewModel<LoginUiState>(LoginUiState(passkeySupported = authenticator.isSupported)) {

    init {
        uiPreferences.state
            .onEach { prefs ->
                setState {
                    if (username.isBlank() && prefs.lastUsername.isNotBlank()) copy(locale = prefs.locale.wire, username = prefs.lastUsername)
                    else copy(locale = prefs.locale.wire)
                }
            }
            .launchIn(viewModelScope)
        sessionExpiry.state
            .onEach { expired -> setState { copy(sessionExpired = expired) } }
            .launchIn(viewModelScope)
        if (continueFromUm.isPending) openFromUm()
    }

    private fun openFromUm() {
        setState { copy(loading = true) }
        launchResult(
            block = { continueFromUm(Unit) },
            onSuccess = { user -> if (user != null) signedIn(user) else setState { copy(loading = false) } },
            onFailure = { e -> setState { copy(loading = false, errorState = LoginUiStateError.LaunchFailed(e)) } },
        )
    }

    fun onUsernameChange(value: String) = setState { copy(username = value, errorState = null) }
    fun onPasswordChange(value: String) = setState { copy(password = value, errorState = null) }

    fun onLocaleChange(value: String) {
        val parsed = LocalePreference.parse(value)
        if (parsed.wire == current.locale) return
        preferences.setLocale(parsed)
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
                preferences.setLastUsername(s.username)
                signedIn(user)
            },
            onFailure = { e ->
                setState { copy(loading = false, errorState = LoginUiStateError.LoginFailed(e), password = "") }
            },
        )
    }

    fun submitPasskey() {
        if (current.loading) return
        setState { copy(loading = true, validationRequested = false, errorState = null) }
        launchResult(
            block = { loginWithPasskey(Unit) },
            onSuccess = { user -> signedIn(user) },
            onFailure = { e -> setState { copy(loading = false, errorState = LoginUiStateError.PasskeyFailed(e)) } },
        )
    }

    private fun signedIn(user: User) {
        sessionExpiry.acknowledge()
        setState { copy(loading = false, loggedInUser = user, password = "") }
    }
}
