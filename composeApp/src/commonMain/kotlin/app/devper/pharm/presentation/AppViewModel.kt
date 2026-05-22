package app.devper.pharm.presentation

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.domain.observer.AuthStateProvider
import app.devper.pharm.domain.observer.OfflineQueueProvider
import app.devper.pharm.domain.observer.UiPreferencesProvider
import app.devper.pharm.domain.usecase.GetProfileUseCase
import app.devper.pharm.domain.usecase.LogoutUseCase
import app.devper.pharm.domain.usecase.SetThemePreferenceUseCase
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AppViewModel(
    authState: AuthStateProvider,
    offlineQueue: OfflineQueueProvider,
    uiPreferences: UiPreferencesProvider,
    private val logout: LogoutUseCase,
    private val getProfile: GetProfileUseCase,
    private val setTheme: SetThemePreferenceUseCase,
) : BaseViewModel<AppUiState>(AppUiState()) {

    init {

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
        )
    }

    fun toggleTheme(currentlyDark: Boolean) {
        setTheme(if (currentlyDark) ThemePreference.Light else ThemePreference.Dark)
    }

    fun dismissError() = setState { copy(error = null) }
}
