package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.User
import app.devper.pharm.domain.param.LoginParam
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>

    suspend fun login(param: LoginParam): User

    suspend fun logout()
}
