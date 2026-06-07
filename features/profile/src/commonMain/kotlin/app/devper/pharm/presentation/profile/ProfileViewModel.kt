package app.devper.pharm.presentation.profile

import app.devper.pharm.common.error.ErrorMessages

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.model.DensityPreference
import app.devper.pharm.domain.model.FontSizePreference
import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.observer.UiPreferencesProvider
import app.devper.pharm.domain.param.ChangePasswordParam
import app.devper.pharm.domain.param.UpdateProfileParam
import app.devper.pharm.domain.usecase.ChangePasswordUseCase
import app.devper.pharm.domain.usecase.GetProfileUseCase
import app.devper.pharm.domain.usecase.SetDensityPreferenceUseCase
import app.devper.pharm.domain.usecase.SetFontSizePreferenceUseCase
import app.devper.pharm.domain.usecase.SetThemePreferenceUseCase
import app.devper.pharm.domain.usecase.UpdateProfileUseCase
import app.devper.pharm.ui.common.BaseFormViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProfileViewModel(
    private val getProfile: GetProfileUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val changePassword: ChangePasswordUseCase,
    uiPreferences: UiPreferencesProvider,
    private val setTheme: SetThemePreferenceUseCase,
    private val setFontSize: SetFontSizePreferenceUseCase,
    private val setDensity: SetDensityPreferenceUseCase,
) : BaseFormViewModel<ProfileUiState>(ProfileUiState()) {

    init {
        load()
        uiPreferences.state
            .onEach { prefs ->
                setState { copy(theme = prefs.theme.wire, fontSize = prefs.fontSize.wire, density = prefs.density.wire) }
            }
            .launchIn(viewModelScope)
    }

    fun reload() = load()

    fun onFirstName(v: String) = patch { copy(firstName = v) }
    fun onLastName(v: String) = patch { copy(lastName = v) }
    fun onPhone(v: String) = patch { copy(phone = v) }
    fun onEmail(v: String) = patch { copy(email = v) }

    fun openPasswordPanel() = setState {
        copy(showPasswordPanel = true, password = PasswordFormFields(), passwordError = null, passwordSaved = false)
    }

    fun closePasswordPanel() = setState { copy(showPasswordPanel = false) }

    fun onOldPassword(v: String) = patchPassword { copy(oldPassword = v) }
    fun onNewPassword(v: String) = patchPassword { copy(newPassword = v) }
    fun onConfirmPassword(v: String) = patchPassword { copy(confirmPassword = v) }

    fun dismissPasswordError() = setState { copy(passwordError = null) }

    fun onThemeChange(value: String) {
        setTheme(ThemePreference.parse(value))
    }

    fun onFontSizeChange(value: String) {
        setFontSize(FontSizePreference.parse(value))
    }

    fun onDensityChange(value: String) {
        setDensity(DensityPreference.parse(value))
    }

    fun submitPasswordChange() {
        val pwd = current.password
        if (!pwd.canSubmit) return
        setState { copy(passwordSaving = true, passwordError = null) }
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
                setState { copy(passwordSaving = false, passwordError = e.message ?: "เปลี่ยนรหัสผ่านไม่สำเร็จ") }
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
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getProfile(Unit) },
            onSuccess = { user ->
                hydrate(user)
                setState { copy(loading = false) }
            },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: ErrorMessages.LOAD_FAILED) } },
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
}
