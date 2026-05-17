package app.devper.pharm.presentation.auth

import app.devper.pharm.domain.model.User
import app.devper.pharm.ui.common.BaseUiState

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    override val loading: Boolean = false,
    override val error: String? = null,
    val loggedInUser: User? = null,
) : BaseUiState
