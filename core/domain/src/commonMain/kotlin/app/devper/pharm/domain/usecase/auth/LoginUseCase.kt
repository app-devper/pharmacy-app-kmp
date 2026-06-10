package app.devper.pharm.domain.usecase.auth

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.User
import app.devper.pharm.domain.param.auth.LoginParam
import app.devper.pharm.domain.repository.auth.AuthRepository

class LoginUseCase(private val repository: AuthRepository, dispatchers: AppDispatchers) : BaseUseCase<LoginParam, User>(dispatchers) {
    override suspend fun execute(param: LoginParam): User = repository.login(param)
    suspend operator fun invoke(username: String, password: String): Result<User> =
        invoke(LoginParam(username = username.trim(), password = password, system = SYSTEM))

    companion object {

        const val SYSTEM: String = "PHARMACY"
    }
}
