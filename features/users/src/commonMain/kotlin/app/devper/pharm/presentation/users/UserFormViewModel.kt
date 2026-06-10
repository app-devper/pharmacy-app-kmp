package app.devper.pharm.presentation.users

import app.devper.pharm.common.error.CommonUiStateError

import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.users.CreateUserParam
import app.devper.pharm.domain.param.users.UpdateUserParam
import app.devper.pharm.domain.usecase.users.CreateUserUseCase
import app.devper.pharm.domain.usecase.users.GetUsersUseCase
import app.devper.pharm.domain.usecase.users.UpdateUserUseCase
import app.devper.pharm.presentation.users.exception.UserFormUiStateError
import app.devper.pharm.ui.common.BaseFormViewModel

class UserFormViewModel(
    private val getUsers: GetUsersUseCase,
    private val createUser: CreateUserUseCase,
    private val updateUser: UpdateUserUseCase,
) : BaseFormViewModel<UserFormUiState>(UserFormUiState()) {

    fun init(mode: UserFormMode) {
        setState { copy(mode = mode) }
        if (mode is UserFormMode.Edit) hydrateForEdit(mode.id)
    }

    fun onFirstName(v: String) = patch { copy(firstName = v) }
    fun onLastName(v: String) = patch { copy(lastName = v) }
    fun onUsername(v: String) = patch { copy(username = v) }
    fun onPassword(v: String) = patch { copy(password = v) }
    fun onPhone(v: String) = patch { copy(phone = v) }
    fun onEmail(v: String) = patch { copy(email = v) }

    override suspend fun persist(): Result<Unit> {
        val f = current.form
        return when (val mode = current.mode) {
            is UserFormMode.Add -> createUser(
                CreateUserParam(
                    firstName = f.firstName.trim(),
                    lastName = f.lastName.trim(),
                    username = f.username.trim(),
                    password = f.password,
                    phone = f.phone.trim(),
                    email = f.email.trim(),
                ),
            ).map { Unit }
            is UserFormMode.Edit -> updateUser(
                UpdateUserParam(
                    id = mode.id,
                    firstName = f.firstName.trim(),
                    lastName = f.lastName.trim(),
                    phone = f.phone.trim(),
                    email = f.email.trim(),
                ),
            ).map { Unit }
        }
    }

    private fun hydrateForEdit(id: String) {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getUsers() },
            onSuccess = { list ->
                val user = list.firstOrNull { it.id == id }
                if (user == null) {
                    setState { copy(loading = false, errorState = UserFormUiStateError.NotFound()) }
                } else {
                    hydrate(user)
                }
            },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
        )
    }

    private fun hydrate(user: UmUser) {
        setState {
            copy(
                loading = false,
                form = UserFormFields(
                    firstName = user.firstName,
                    lastName = user.lastName,
                    username = user.username,
                    password = "",
                    phone = user.phone,
                    email = user.email,
                ),
            )
        }
    }

    private fun patch(transform: UserFormFields.() -> UserFormFields) {
        setState { copy(form = form.transform()) }
    }
}
