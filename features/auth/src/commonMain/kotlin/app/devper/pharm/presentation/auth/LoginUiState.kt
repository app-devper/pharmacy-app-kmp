package app.devper.pharm.presentation.auth

import app.devper.pharm.domain.model.User
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.LoadableUiState

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val locale: String = "th",
    val validationRequested: Boolean = false,
    val sessionExpired: Boolean = false,
    override val loading: Boolean = false,
    val errorState: AppException? = null,
    val loggedInUser: User? = null,
) : LoadableUiState<LoginUiState> {

    override val domainError: AppException? get() = errorState
    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val usernameMissing: Boolean = validationRequested && username.isBlank()
    val passwordMissing: Boolean = validationRequested && password.isBlank()
}
