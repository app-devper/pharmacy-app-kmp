package app.devper.pharm.data.repository

import app.devper.pharm.common.AuthException
import app.devper.pharm.common.LaunchExpiredException
import app.devper.pharm.common.LaunchRefusedException
import app.devper.pharm.data.network.AppJson
import app.devper.pharm.data.remote.api.AuthApi
import app.devper.pharm.data.remote.dto.PasskeyLoginOptionsRequest
import app.devper.pharm.data.remote.dto.PasskeyLoginRequest
import app.devper.pharm.data.remote.dto.TicketRequest
import app.devper.pharm.data.remote.dto.UserInfoResponse
import app.devper.pharm.data.repository.internal.toRequest
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.domain.model.PasskeyChallenge
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.User
import app.devper.pharm.domain.param.auth.LoginParam
import app.devper.pharm.domain.repository.auth.AuthRepository
import app.devper.pharm.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonElement

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = tokenStorage.tokenFlow.map { !it.isNullOrBlank() }

    override suspend fun login(param: LoginParam): User {
        val tokenRes = api.login(param.toRequest())
        return start(tokenRes.accessToken)
    }

    override suspend fun exchangeTicket(ticket: String): User {
        tokenStorage.clear()
        val tokenRes = try {
            api.exchange(TicketRequest(ticket))
        } catch (e: AuthException) {
            throw LaunchExpiredException(cause = e)
        }
        return start(tokenRes.accessToken, expectedSystem = LoginUseCase.SYSTEM)
    }

    override suspend fun passkeyLoginOptions(): PasskeyChallenge {
        val res = api.passkeyLoginOptions(PasskeyLoginOptionsRequest(LoginUseCase.SYSTEM))
        return PasskeyChallenge(res.ceremonyId, AppJson.encodeToString(JsonElement.serializer(), res.options))
    }

    override suspend fun loginWithPasskey(ceremonyId: String, credentialJson: String): User {
        val tokenRes = api.passkeyLogin(PasskeyLoginRequest(ceremonyId, AppJson.parseToJsonElement(credentialJson)))
        return start(tokenRes.accessToken)
    }

    override suspend fun logout() {
        try {
            api.logout()
        } finally {
            tokenStorage.clear()
        }
    }

    private suspend fun start(accessToken: String, expectedSystem: String? = null): User {
        tokenStorage.save(accessToken)
        try {
            if (expectedSystem != null && api.getSystem().systemCode != expectedSystem) {
                throw LaunchRefusedException()
            }
            return api.getUserInfo().toUser()
        } catch (e: Throwable) {
            tokenStorage.clear()
            throw e
        }
    }

    private fun UserInfoResponse.toUser(): User = User(
        id = id,
        username = username,
        displayName = listOfNotNull(firstName, lastName)
            .joinToString(" ")
            .ifBlank { username },
        role = Role.parse(role),
    )
}
