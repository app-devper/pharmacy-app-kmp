package app.devper.pharm.presentation.users

import app.devper.pharm.domain.repository.users.UsersRepository
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.users.CreateUserParam
import app.devper.pharm.domain.param.users.UpdateUserParam
import app.devper.pharm.presentation.users.exception.UserFormUiStateError
import app.devper.pharm.ui.common.BaseEditFormViewModel

class UserFormViewModel(
    private val users: UsersRepository,
) : BaseEditFormViewModel<UserFormUiState, UserFormFields>(UserFormUiState()) {

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

    private fun hydrateForEdit(id: String) = hydrate(
        fetch = { users.list().firstOrNull { it.id == id } },
        notFound = { UserFormUiStateError.NotFound() },
        failure = { e -> UserFormUiStateError.LoadUserFailed(e) },
        toForm = ::formOf,
    )

    private fun formOf(user: UmUser) = UserFormFields(
        firstName = user.firstName,
        lastName = user.lastName,
        username = user.username,
        password = "",
        phone = user.phone,
        email = user.email,
    )

    private fun patch(transform: UserFormFields.() -> UserFormFields) {
        setState { copy(form = form.transform()) }
    }
}
