package app.devper.pharm.presentation.profile

import app.devper.pharm.domain.model.UmUser
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
    val canSubmit: Boolean get() = oldPassword.isNotBlank() && matches
}

data class ProfileUiState(
    val user: UmUser? = null,
    val form: ProfileFormFields = ProfileFormFields(),
    val password: PasswordFormFields = PasswordFormFields(),
    val showPasswordPanel: Boolean = false,
    val passwordSaving: Boolean = false,
    val passwordSaved: Boolean = false,
    val passwordError: String? = null,
    val theme: String = "auto",
    val fontSize: String = "md",
    val density: String = "comfortable",
    val locale: String = "system",
    val localeChangeMessage: String? = null,
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    override val error: String? = null,
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

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override fun withError(error: String?) = copy(error = error)
}
