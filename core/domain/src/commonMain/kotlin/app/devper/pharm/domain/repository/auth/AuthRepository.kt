package app.devper.pharm.domain.repository.auth

import app.devper.pharm.domain.model.User
import app.devper.pharm.domain.param.auth.LoginParam
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>

    suspend fun login(param: LoginParam): User

    suspend fun logout()
}
