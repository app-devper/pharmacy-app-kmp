package app.devper.pharm.presentation.users

import app.devper.pharm.domain.repository.users.UsersRepository
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.users.SetUserPasswordParam
import app.devper.pharm.domain.param.users.SetUserRoleParam
import app.devper.pharm.domain.param.users.SetUserStatusParam
import app.devper.pharm.domain.usecase.profile.GetProfileUseCase
import app.devper.pharm.presentation.users.exception.UsersUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel

class UsersListViewModel(
    private val getProfile: GetProfileUseCase,
    private val users: UsersRepository,
) : BaseLoadableViewModel<UsersListUiState>(UsersListUiState()) {

    init {
        loadProfile()
        reload()
    }

    fun reload() {
        load(
            fetch = { Result.success(users.list()) },
            failure = { e -> UsersUiStateError.LoadUsersFailed(e) },
            onLoaded = { list -> copy(users = list) },
        )
        launchResult(
            block = { Result.success(users.rules()) },
            onSuccess = { rules -> setState { copy(creatableRoles = rules.creatableRoles) } },
            onFailure = { setState { copy(creatableRoles = emptyList()) } },
        )
    }

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
            block = { Result.success(users.delete(target.id)) },
            onSuccess = {
                dismissAction()
                reload()
            },
            onFailure = { e ->
                setState { copy(actionBusy = false, actionTarget = null, actionMode = null, errorState = CommonUiStateError.DeleteFailed(e)) }
            },
        )
    }

    fun submitRoleChange(role: Role) {
        val target = current.actionTarget ?: return
        if (role == Role.UNKNOWN) return
        setState { copy(actionBusy = true) }
        launchResult(
            block = { Result.success(users.setRole(SetUserRoleParam(target.id, role))) },
            onSuccess = {
                dismissAction()
                reload()
            },
            onFailure = { e ->
                setState { copy(actionBusy = false, actionTarget = null, actionMode = null, errorState = UsersUiStateError.RoleChangeFailed(e)) }
            },
        )
    }

    fun confirmStatusToggle() {
        val target = current.actionTarget ?: return
        val next = if (target.status.isActive) UmStatus.INACTIVE else UmStatus.ACTIVE
        setState { copy(actionBusy = true) }
        launchResult(
            block = { Result.success(users.setStatus(SetUserStatusParam(target.id, next))) },
            onSuccess = {
                dismissAction()
                reload()
            },
            onFailure = { e ->
                setState { copy(actionBusy = false, actionTarget = null, actionMode = null, errorState = UsersUiStateError.StatusChangeFailed(e)) }
            },
        )
    }

    fun submitPasswordSet(newPassword: String) {
        val target = current.actionTarget ?: return
        if (newPassword.isBlank()) return
        setState { copy(actionBusy = true) }
        launchResult(
            block = { Result.success(users.setPassword(SetUserPasswordParam(target.id, newPassword))) },
            onSuccess = {
                dismissAction()
                reload()
            },
            onFailure = { e ->
                setState { copy(actionBusy = false, actionTarget = null, actionMode = null, errorState = UsersUiStateError.SetPasswordFailed(e)) }
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
