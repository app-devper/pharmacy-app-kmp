package app.devper.pharm.presentation.auth

import app.devper.pharm.common.userMessageOr
import app.devper.pharm.domain.usecase.LoginUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

private const val LOGIN_REQUIRED_FIELDS = "กรุณากรอกชื่อผู้ใช้และรหัสผ่าน"
private const val LOGIN_FAILED = "เข้าสู่ระบบไม่สำเร็จ"

class LoginViewModel(
    private val login: LoginUseCase,
) : BaseLoadableViewModel<LoginUiState>(LoginUiState()) {

    fun onUsernameChange(value: String) = setState { copy(username = value, error = null) }
    fun onPasswordChange(value: String) = setState { copy(password = value, error = null) }

    fun submit() {
        val s = current
        if (s.username.isBlank() || s.password.isBlank()) {
            setState { copy(error = LOGIN_REQUIRED_FIELDS) }
            return
        }
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { login(s.username, s.password) },
            onSuccess = { user -> setState { copy(loading = false, loggedInUser = user, password = "") } },
            onFailure = { e ->
                setState { copy(loading = false, error = e.userMessageOr(LOGIN_FAILED), password = "") }
            },
        )
    }
}
