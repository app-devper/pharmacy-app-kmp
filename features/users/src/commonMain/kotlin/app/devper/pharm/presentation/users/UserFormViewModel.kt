package app.devper.pharm.presentation.users

import app.devper.pharm.domain.repository.users.UsersRepository
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.users.CreateUserParam
import app.devper.pharm.domain.param.users.UpdateUserParam
import app.devper.pharm.presentation.users.exception.UserFormUiStateError
import app.devper.pharm.ui.common.BaseFormViewModel

class UserFormViewModel(
    private val users: UsersRepository,
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
        when (val mode = current.mode) {
            is UserFormMode.Add -> users.create(
                CreateUserParam(
                    firstName = f.firstName.trim(),
                    lastName = f.lastName.trim(),
                    username = f.username.trim(),
                    password = f.password,
                    phone = f.phone.trim(),
                    email = f.email.trim(),
                ),
            )
            is UserFormMode.Edit -> users.update(
                UpdateUserParam(
                    id = mode.id,
                    firstName = f.firstName.trim(),
                    lastName = f.lastName.trim(),
                    phone = f.phone.trim(),
                    email = f.email.trim(),
                ),
            )
        }
        return Result.success(Unit)
    }

    private fun hydrateForEdit(id: String) {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { Result.success(users.list()) },
            onSuccess = { list ->
                val user = list.firstOrNull { it.id == id }
                if (user == null) {
                    setState { copy(loading = false, errorState = UserFormUiStateError.NotFound()) }
                } else {
                    hydrate(user)
                }
            },
            onFailure = { e ->
                setState { copy(loading = false, errorState = UserFormUiStateError.LoadUserFailed(e)) }
            },
        )
    }

    private fun hydrate(user: UmUser) {
        val hydratedForm = UserFormFields(
            firstName = user.firstName,
            lastName = user.lastName,
            username = user.username,
            password = "",
            phone = user.phone,
            email = user.email,
        )
        setState {
            copy(
                loading = false,
                form = hydratedForm,
                baselineForm = hydratedForm,
            )
        }
    }

    private fun patch(transform: UserFormFields.() -> UserFormFields) {
        setState { copy(form = form.transform()) }
    }
}
