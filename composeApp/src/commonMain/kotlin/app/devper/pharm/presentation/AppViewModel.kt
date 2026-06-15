package app.devper.pharm.presentation

import app.devper.pharm.presentation.exception.AppUiStateError

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.model.LocalePreference
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.domain.observer.AuthStateProvider
import app.devper.pharm.domain.observer.OfflineAutoSync
import app.devper.pharm.domain.observer.OfflineQueueProvider
import app.devper.pharm.domain.observer.UiPreferencesProvider
import app.devper.pharm.domain.usecase.profile.GetProfileUseCase
import app.devper.pharm.domain.usecase.auth.LogoutUseCase
import app.devper.pharm.domain.usecase.settings.SetLocalePreferenceUseCase
import app.devper.pharm.domain.usecase.settings.SetThemePreferenceUseCase
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AppViewModel(
    authState: AuthStateProvider,
    offlineQueue: OfflineQueueProvider,
    uiPreferences: UiPreferencesProvider,
    offlineAutoSync: OfflineAutoSync,
    private val logout: LogoutUseCase,
    private val getProfile: GetProfileUseCase,
    private val setTheme: SetThemePreferenceUseCase,
    private val setLocale: SetLocalePreferenceUseCase,
) : BaseViewModel<AppUiState>(AppUiState()) {

    init {

        offlineAutoSync.start(viewModelScope)

        authState.isLoggedIn
            .onEach { loggedIn ->
                setState { copy(isLoggedIn = loggedIn) }
                if (loggedIn) loadRole() else setState {
                    copy(role = Role.UNKNOWN, userDisplayName = "", userInitial = "")
                }
            }
            .launchIn(viewModelScope)

        offlineQueue.pending
            .onEach { queue -> setState { copy(pendingSyncCount = queue.size) } }
            .launchIn(viewModelScope)

        uiPreferences.state
            .onEach { prefs -> setState { copy(uiPreferences = prefs) } }
            .launchIn(viewModelScope)
    }

    private fun loadRole() {
        launchResult(
            block = { getProfile(Unit) },
            onSuccess = { user ->
                setState {
                    copy(
                        role = user.role,
                        userDisplayName = user.displayName,
                        userInitial = user.initials,
                    )
                }
            },
            onFailure = {
                setState { copy(role = Role.UNKNOWN, userDisplayName = "", userInitial = "") }
            },
        )
    }

    fun signOut() {
        launchResult(
            block = { logout() },
            onSuccess = {  },
            onFailure = { e -> setState { copy(errorState = AppUiStateError.LogoutFailed(e)) } },
        )
    }

    fun toggleTheme(currentlyDark: Boolean) {
        setTheme(if (currentlyDark) ThemePreference.Light else ThemePreference.Dark)
            .onFailure { e -> setState { copy(errorState = AppUiStateError.ThemeChangeFailed(e)) } }
    }

    fun onLocaleChange(value: String) {
        val parsed = LocalePreference.parse(value)
        if (parsed.wire == current.uiPreferences.locale.wire) return
        setLocale(parsed)
    }

    fun dismissError() = setState { copy(errorState = null) }
}
