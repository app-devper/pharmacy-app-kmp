package app.devper.pharm.domain.repository

import app.devper.pharm.domain.repository.auth.AuthRepository

import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.User
import app.devper.pharm.domain.param.auth.LoginParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthRepository(
    private val loginResult: User = User(
        id = "u-1",
        username = "",
        displayName = "Default User",
        role = Role.USER,
    ),
    private val loginThrowsOn: String? = null,
) : AuthRepository {

    private val isLoggedInState = MutableStateFlow(false)
    override val isLoggedIn: Flow<Boolean> = isLoggedInState.asStateFlow()

    var lastLogin: LoginParam? = null
        private set
    var lastLogoutCalled: Boolean = false
        private set

    override suspend fun login(param: LoginParam): User {
        if (param.username == loginThrowsOn) {
            throw RuntimeException("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง")
        }
        lastLogin = param
        isLoggedInState.value = true
        return loginResult.copy(username = param.username)
    }

    override suspend fun logout() {
        lastLogoutCalled = true
        isLoggedInState.value = false
    }
}
