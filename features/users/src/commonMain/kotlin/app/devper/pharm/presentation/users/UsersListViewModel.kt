package app.devper.pharm.presentation.users

import app.devper.pharm.common.error.ErrorMessages

import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.SetUserPasswordParam
import app.devper.pharm.domain.param.SetUserRoleParam
import app.devper.pharm.domain.param.SetUserStatusParam
import app.devper.pharm.domain.usecase.DeleteUserUseCase
import app.devper.pharm.domain.usecase.GetProfileUseCase
import app.devper.pharm.domain.usecase.GetUsersUseCase
import app.devper.pharm.domain.usecase.SetUserPasswordUseCase
import app.devper.pharm.domain.usecase.SetUserRoleUseCase
import app.devper.pharm.domain.usecase.SetUserStatusUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

class UsersListViewModel(
    private val getProfile: GetProfileUseCase,
    private val getUsers: GetUsersUseCase,
    private val deleteUser: DeleteUserUseCase,
    private val setUserRole: SetUserRoleUseCase,
    private val setUserStatus: SetUserStatusUseCase,
    private val setUserPassword: SetUserPasswordUseCase,
) : BaseLoadableViewModel<UsersListUiState>(UsersListUiState()) {

    init {
        loadProfile()
        reload()
    }

    fun reload() = launchLoad(
        block = { getUsers() },
        onSuccess = { list -> copy(users = list) },
    )

    fun setSearch(value: String) = setState { copy(searchQuery = value) }

    fun requestDelete(target: UmUser) = setState { copy(actionTarget = target, actionMode = UsersAction.Delete) }
    fun requestRoleEdit(target: UmUser) = setState { copy(actionTarget = target, actionMode = UsersAction.EditRole) }
    fun requestStatusToggle(target: UmUser) = setState { copy(actionTarget = target, actionMode = UsersAction.ToggleStatus) }
    fun requestPasswordSet(target: UmUser) = setState { copy(actionTarget = target, actionMode = UsersAction.SetPassword) }
    fun dismissAction() = setState { copy(actionTarget = null, actionMode = null, actionBusy = false) }

    fun confirmDelete() {
        val target = current.actionTarget ?: return
        setState { copy(actionBusy = true) }
        launchResult(
            block = { deleteUser(target.id) },
            onSuccess = {
                dismissAction()
                reload()
            },
            onFailure = { e ->
                setState { copy(actionBusy = false, actionTarget = null, actionMode = null, error = e.message ?: ErrorMessages.DELETE_FAILED) }
            },
        )
    }

    fun submitRoleChange(role: Role) {
        val target = current.actionTarget ?: return
        if (role == Role.UNKNOWN) return
        setState { copy(actionBusy = true) }
        launchResult(
            block = { setUserRole(SetUserRoleParam(target.id, role)) },
            onSuccess = {
                dismissAction()
                reload()
            },
            onFailure = { e ->
                setState { copy(actionBusy = false, actionTarget = null, actionMode = null, error = e.message ?: "เปลี่ยน Role ไม่สำเร็จ") }
            },
        )
    }

    fun confirmStatusToggle() {
        val target = current.actionTarget ?: return
        val next = if (target.status.isActive) UmStatus.INACTIVE else UmStatus.ACTIVE
        setState { copy(actionBusy = true) }
        launchResult(
            block = { setUserStatus(SetUserStatusParam(target.id, next)) },
            onSuccess = {
                dismissAction()
                reload()
            },
            onFailure = { e ->
                setState { copy(actionBusy = false, actionTarget = null, actionMode = null, error = e.message ?: "เปลี่ยนสถานะไม่สำเร็จ") }
            },
        )
    }

    fun submitPasswordSet(newPassword: String) {
        val target = current.actionTarget ?: return
        if (newPassword.isBlank()) return
        setState { copy(actionBusy = true) }
        launchResult(
            block = { setUserPassword(SetUserPasswordParam(target.id, newPassword)) },
            onSuccess = {
                dismissAction()
                reload()
            },
            onFailure = { e ->
                setState { copy(actionBusy = false, actionTarget = null, actionMode = null, error = e.message ?: "ตั้งรหัสผ่านไม่สำเร็จ") }
            },
        )
    }

    private fun loadProfile() {
        launchResult(
            block = { getProfile(Unit) },
            onSuccess = { me -> setState { copy(currentUserId = me.id, currentUserRole = me.role) } },
            onFailure = {  },
        )
    }
}
