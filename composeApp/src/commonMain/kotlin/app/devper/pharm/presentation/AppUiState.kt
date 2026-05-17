package app.devper.pharm.presentation

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.common.BaseUiState

data class AppUiState(
    val isLoggedIn: Boolean = false,
    val pendingSyncCount: Int = 0,
    val role: Role = Role.UNKNOWN,
    val userDisplayName: String = "",
    val userInitial: String = "",
    override val loading: Boolean = false,
    override val error: String? = null,
) : BaseUiState
