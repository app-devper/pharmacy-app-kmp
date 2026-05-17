package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.AuthApi
import app.devper.pharm.data.remote.dto.LoginRequest
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.User
import app.devper.pharm.domain.param.LoginParam
import app.devper.pharm.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = tokenStorage.tokenFlow.map { !it.isNullOrBlank() }

    override suspend fun login(param: LoginParam): User {

        val tokenRes = api.login(param.toRequest())
        tokenStorage.save(tokenRes.accessToken)

        val info = try {
            api.getUserInfo()
        } catch (e: Throwable) {
            tokenStorage.clear()
            throw e
        }
        return User(
            id = info.id,
            username = info.username,
            displayName = listOfNotNull(info.firstName, info.lastName)
                .joinToString(" ")
                .ifBlank { info.username },
            role = Role.parse(info.role),
        )
    }

    override suspend fun logout() {
        try {
            api.logout()
        } finally {
            tokenStorage.clear()
        }
    }

    private fun LoginParam.toRequest() = LoginRequest(
        username = username,
        password = password,
        system = system,
    )
}
