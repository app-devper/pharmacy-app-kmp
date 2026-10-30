package app.devper.pharm.presentation

import app.devper.pharm.presentation.exception.AppUiStateError

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.domain.observer.AuthStateProvider
import app.devper.pharm.domain.observer.OfflineAutoSync
import app.devper.pharm.domain.pendingsales.PendingSales
import app.devper.pharm.domain.observer.UiPreferencesProvider
import app.devper.pharm.domain.observer.SignedInUser
import app.devper.pharm.domain.repository.auth.AuthRepository
import app.devper.pharm.domain.repository.settings.UiPreferencesRepository
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.onEach

class AppViewModel(
    authState: AuthStateProvider,
    pendingSales: PendingSales,
    uiPreferences: UiPreferencesProvider,
    private val offlineAutoSync: OfflineAutoSync,
    private val auth: AuthRepository,
    private val signedInUser: SignedInUser,
    private val preferences: UiPreferencesRepository,
) : BaseViewModel<AppUiState>(AppUiState()) {

    init {

        offlineAutoSync.start(viewModelScope)

        authState.isLoggedIn
            .onEach { loggedIn ->
                setState { copy(isLoggedIn = loggedIn) }
                if (loggedIn) loadUser() else signedInUser.clear()
            }
            .launchIn(viewModelScope)

        signedInUser.user
            .onEach { user ->
                setState {
                    copy(
                        role = user?.role ?: Role.UNKNOWN,
                        userDisplayName = user?.displayName.orEmpty(),
                        userInitial = user?.initials.orEmpty(),
                    )
                }
            }
            .launchIn(viewModelScope)

        pendingSales.entries
            .onEach { queue -> setState { copy(pendingSyncCount = queue.size) } }
            .launchIn(viewModelScope)

        uiPreferences.state
            .onEach { prefs -> setState { copy(uiPreferences = prefs) } }
            .launchIn(viewModelScope)
    }

    private fun loadUser() {
        launchResult(
            block = { Result.success(signedInUser.load()) },
            onSuccess = { },
            onFailure = { signedInUser.clear() },
        )
    }

    fun syncNow() {
        viewModelScope.launch { offlineAutoSync.syncPending() }
    }

    fun signOut() {
        launchResult(
            block = { Result.success(auth.logout()) },
            onSuccess = {  },
            onFailure = { e -> setState { copy(errorState = AppUiStateError.LogoutFailed(e)) } },
        )
    }

    fun toggleTheme(currentlyDark: Boolean) {
        runCatching { preferences.setTheme(if (currentlyDark) ThemePreference.Light else ThemePreference.Dark) }
            .onFailure { e -> setState { copy(errorState = AppUiStateError.ThemeChangeFailed(e)) } }
    }

    fun dismissError() = setState { copy(errorState = null) }
}
