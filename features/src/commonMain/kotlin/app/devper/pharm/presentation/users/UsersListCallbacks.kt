package app.devper.pharm.presentation.users

import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmUser

data class UsersListCallbacks(
    val onSearch: (String) -> Unit,
    val onAddUser: () -> Unit,
    val onEditUser: (UmUser) -> Unit,
    val onRequestDelete: (UmUser) -> Unit,
    val onConfirmDelete: () -> Unit,
    val onRequestRoleEdit: (UmUser) -> Unit,
    val onSubmitRoleChange: (Role) -> Unit,
    val onRequestStatusToggle: (UmUser) -> Unit,
    val onConfirmStatusToggle: () -> Unit,
    val onRequestPasswordSet: (UmUser) -> Unit,
    val onSubmitPasswordSet: (String) -> Unit,
    val onDismissAction: () -> Unit,
    val onDismissError: () -> Unit,
) {
    companion object {
        val Preview = UsersListCallbacks(
            onSearch = {}, onAddUser = {}, onEditUser = {},
            onRequestDelete = {}, onConfirmDelete = {},
            onRequestRoleEdit = {}, onSubmitRoleChange = {},
            onRequestStatusToggle = {}, onConfirmStatusToggle = {},
            onRequestPasswordSet = {}, onSubmitPasswordSet = {},
            onDismissAction = {}, onDismissError = {},
        )
    }
}
