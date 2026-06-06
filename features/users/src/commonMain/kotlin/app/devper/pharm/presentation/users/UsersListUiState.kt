package app.devper.pharm.presentation.users

import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.ui.common.BaseUiState

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
    override val error: String? = null,
) : BaseUiState {

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
