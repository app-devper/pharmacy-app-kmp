package app.devper.pharm.domain.repository

import app.devper.pharm.common.LaunchExpiredException
import app.devper.pharm.common.PasskeyRejectedException
import app.devper.pharm.domain.model.PasskeyChallenge
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.User
import app.devper.pharm.domain.param.auth.LoginParam
import app.devper.pharm.domain.repository.auth.AuthRepository
import kotlinx.coroutines.awaitCancellation
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
    private val acceptedTicket: String = "good-ticket",
    private val rejectPasskey: Boolean = false,
    private val hangExchange: Boolean = false,
) : AuthRepository {

    private val isLoggedInState = MutableStateFlow(false)
    override val isLoggedIn: Flow<Boolean> = isLoggedInState.asStateFlow()

    var lastLogin: LoginParam? = null
        private set
    var lastLogoutCalled: Boolean = false
        private set
    val exchangedTickets = mutableListOf<String>()
    val passkeyLogins = mutableListOf<Pair<String, String>>()

    override suspend fun login(param: LoginParam): User {
        if (param.username == loginThrowsOn) {
            throw RuntimeException("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง")
        }
        lastLogin = param
        isLoggedInState.value = true
        return loginResult.copy(username = param.username)
    }

    override suspend fun exchangeTicket(ticket: String): User {
        exchangedTickets += ticket
        if (hangExchange) awaitCancellation()
        if (ticket != acceptedTicket) throw LaunchExpiredException()
        isLoggedInState.value = true
        return loginResult.copy(username = "from-um")
    }

    override suspend fun passkeyLoginOptions(): PasskeyChallenge = PasskeyChallenge("c1", "{\"publicKey\":{}}")

    override suspend fun loginWithPasskey(ceremonyId: String, credentialJson: String): User {
        passkeyLogins += ceremonyId to credentialJson
        if (rejectPasskey) throw PasskeyRejectedException()
        isLoggedInState.value = true
        return loginResult.copy(username = "passkey-user")
    }

    override suspend fun logout() {
        lastLogoutCalled = true
        isLoggedInState.value = false
    }
}
