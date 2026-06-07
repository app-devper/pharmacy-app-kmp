package app.devper.pharm.presentation.auth

import app.devper.pharm.domain.model.User
import app.devper.pharm.ui.common.LoadableUiState

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val locale: String = "system",
    override val loading: Boolean = false,
    override val error: String? = null,
    val loggedInUser: User? = null,
) : LoadableUiState<LoginUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withError(value: String?) = copy(error = value)
}
