package app.devper.pharm.presentation

import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UiPreferences
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.BaseUiState

data class AppUiState(
    val isLoggedIn: Boolean = false,
    val pendingSyncCount: Int = 0,
    val role: Role = Role.UNKNOWN,
    val userDisplayName: String = "",
    val userInitial: String = "",
    val uiPreferences: UiPreferences = UiPreferences.Default,
    override val loading: Boolean = false,
    val errorState: AppException? = null,
) : BaseUiState {
    override val domainError: AppException? get() = errorState
}
