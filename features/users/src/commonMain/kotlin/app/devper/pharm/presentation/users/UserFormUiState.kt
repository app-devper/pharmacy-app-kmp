package app.devper.pharm.presentation.users

import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.BaseFormUiState

sealed interface UserFormMode {
    data object Add : UserFormMode
    data class Edit(val id: String) : UserFormMode
}

data class UserFormFields(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val password: String = "",
    val phone: String = "",
    val email: String = "",
)

data class UserFormUiState(
    val mode: UserFormMode = UserFormMode.Add,
    val form: UserFormFields = UserFormFields(),
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    val errorState: AppException? = null,
) : BaseFormUiState<UserFormUiState> {

    val isEdit: Boolean get() = mode is UserFormMode.Edit

    override val canSubmit: Boolean
        get() {
            if (saving || loading) return false
            if (form.firstName.isBlank()) return false
            return if (isEdit) true
            else form.username.isNotBlank() && form.password.length >= 8
        }

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
