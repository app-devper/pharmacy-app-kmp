package app.devper.pharm.presentation.auth

import app.devper.pharm.domain.usecase.LoginUseCase
import app.devper.pharm.ui.common.BaseViewModel

class LoginViewModel(
    private val login: LoginUseCase,
) : BaseViewModel<LoginUiState>(LoginUiState()) {

    fun onUsernameChange(value: String) = setState { copy(username = value, error = null) }
    fun onPasswordChange(value: String) = setState { copy(password = value, error = null) }
    fun dismissError() = setState { copy(error = null) }

    fun submit() {
        val s = current
        if (s.username.isBlank() || s.password.isBlank()) {
            setState { copy(error = "กรุณากรอกชื่อผู้ใช้และรหัสผ่าน") }
            return
        }
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { login(s.username, s.password) },
            onSuccess = { user -> setState { copy(loading = false, loggedInUser = user, password = "") } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "เข้าสู่ระบบไม่สำเร็จ", password = "") } },
        )
    }
}
