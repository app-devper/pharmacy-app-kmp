package app.devper.pharm.presentation.profile

import app.devper.pharm.presentation.profile.exception.ProfileUiStateError


import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.profile.ChangePasswordParam
import app.devper.pharm.domain.param.profile.UpdateProfileParam
import app.devper.pharm.domain.usecase.profile.ChangePasswordUseCase
import app.devper.pharm.domain.usecase.profile.GetProfileUseCase
import app.devper.pharm.domain.usecase.profile.UpdateProfileUseCase
import app.devper.pharm.ui.common.BaseFormViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProfileViewModel(
    private val getProfile: GetProfileUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val changePassword: ChangePasswordUseCase,
) : BaseFormViewModel<ProfileUiState>(ProfileUiState()) {

    init {
        load()
    }

    fun reload() = load()

    fun onFirstName(v: String) = patch { copy(firstName = v) }
    fun onLastName(v: String) = patch { copy(lastName = v) }
    fun onPhone(v: String) = patch { copy(phone = v) }
    fun onEmail(v: String) = patch { copy(email = v) }

    fun openPasswordPanel() = setState {
        copy(showPasswordPanel = true, password = PasswordFormFields(), passwordErrorState = null, passwordSaved = false)
    }

    fun closePasswordPanel() = setState { copy(showPasswordPanel = false) }

    fun onOldPassword(v: String) = patchPassword { copy(oldPassword = v) }
    fun onNewPassword(v: String) = patchPassword { copy(newPassword = v) }
    fun onConfirmPassword(v: String) = patchPassword { copy(confirmPassword = v) }

    fun dismissPasswordError() = setState { copy(passwordErrorState = null) }

    fun submitPasswordChange() {
        val pwd = current.password
        if (!pwd.canSubmit) return
        setState { copy(passwordSaving = true, passwordErrorState = null) }
        launchResult(
            block = { changePassword(ChangePasswordParam(pwd.oldPassword, pwd.newPassword)) },
            onSuccess = {
                setState {
                    copy(
                        passwordSaving = false,
                        passwordSaved = true,
                        showPasswordPanel = false,
                        password = PasswordFormFields(),
                    )
                }
            },
            onFailure = { e ->
                setState { copy(passwordSaving = false, passwordErrorState = ProfileUiStateError.PasswordChangeFailed(e)) }
            },
        )
    }

    override suspend fun persist(): Result<Unit> {
        val f = current.form
        return updateProfile(
            UpdateProfileParam(
                firstName = f.firstName.trim(),
                lastName = f.lastName.trim(),
                phone = f.phone.trim(),
                email = f.email.trim(),
            ),
        ).onSuccess { user -> hydrate(user) }.map { Unit }
    }

    private fun load() {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getProfile(Unit) },
            onSuccess = { user ->
                hydrate(user)
                setState { copy(loading = false) }
            },
            onFailure = { e -> setState { copy(loading = false, errorState = ProfileUiStateError.LoadProfileFailed(e)) } },
        )
    }

    private fun hydrate(user: UmUser) {
        setState {
            copy(
                user = user,
                form = ProfileFormFields(
                    firstName = user.firstName,
                    lastName = user.lastName,
                    phone = user.phone,
                    email = user.email,
                ),
            )
        }
    }

    private fun patch(transform: ProfileFormFields.() -> ProfileFormFields) {
        setState { copy(form = form.transform()) }
    }

    private fun patchPassword(transform: PasswordFormFields.() -> PasswordFormFields) {
        setState { copy(password = password.transform()) }
    }

    private companion object {
    }
}
