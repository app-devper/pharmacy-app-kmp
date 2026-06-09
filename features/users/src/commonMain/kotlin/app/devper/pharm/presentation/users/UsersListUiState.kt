package app.devper.pharm.presentation.users

import app.devper.pharm.common.AppException

import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.ui.common.LoadableUiState

enum class UsersAction { Delete, EditRole, ToggleStatus, SetPassword }

data class UsersListUiState(
    val users: List<UmUser> = emptyList(),
    val searchQuery: String = "",
    val currentUserId: String? = null,
    val currentUserRole: Role = Role.UNKNOWN,
    val actionTarget: UmUser? = null,
    val actionMode: UsersAction? = null,
    val actionBusy: Boolean = false,
    override val loading: Boolean = false,
    val errorState: AppException? = null,
) : LoadableUiState<UsersListUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override val domainError: AppException? get() = errorState
    override fun withError(value: String?) = if (value == null) copy(errorState = null) else this

    val filtered: List<UmUser> = if (searchQuery.isBlank()) {
        users
    } else {
        val q = searchQuery.trim().lowercase()
        users.filter { u ->
            u.firstName.lowercase().contains(q) ||
                u.lastName.lowercase().contains(q) ||
                u.username.lowercase().contains(q) ||
                u.email.lowercase().contains(q)
        }
    }
}
