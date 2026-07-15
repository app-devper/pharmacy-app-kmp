package app.devper.pharm.presentation.profile

import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.BaseFormUiState

data class ProfileFormFields(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
)

data class PasswordFormFields(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
) {
    val matches: Boolean get() = newPassword.isNotBlank() && newPassword == confirmPassword
    val newPasswordValid: Boolean get() = newPassword.length >= 8
    val canSubmit: Boolean get() = oldPassword.isNotBlank() && newPasswordValid && matches
}

data class ProfileUiState(
    val user: UmUser? = null,
    val form: ProfileFormFields = ProfileFormFields(),
    val password: PasswordFormFields = PasswordFormFields(),
    val showPasswordPanel: Boolean = false,
    val passwordSaving: Boolean = false,
    val passwordSaved: Boolean = false,
    val passwordErrorState: AppException? = null,
    val theme: String = "auto",
    val fontSize: String = "md",
    val density: String = "comfortable",
    val locale: String = "th",
    val localeChangeApplied: Boolean = false,
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    val errorState: AppException? = null,
) : BaseFormUiState<ProfileUiState> {
    override val canSubmit: Boolean
        get() = !saving && !loading && user != null && isDirty && form.firstName.isNotBlank()

    val isDirty: Boolean
        get() = user?.let {
            form.firstName != it.firstName ||
                form.lastName != it.lastName ||
                form.phone != it.phone ||
                form.email != it.email
        } == true

    val canAttemptSubmit: Boolean
        get() = !saving && !loading && user != null && isDirty

    override val hasUnsavedChanges: Boolean
        get() = isDirty || showPasswordPanel && password != PasswordFormFields()

    override val domainError: AppException? get() = errorState
    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
