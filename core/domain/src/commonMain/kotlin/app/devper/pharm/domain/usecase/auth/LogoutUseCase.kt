package app.devper.pharm.domain.usecase.auth

import app.devper.pharm.domain.usecase.BaseQueryUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.repository.auth.AuthRepository

class LogoutUseCase(private val repository: AuthRepository, dispatchers: AppDispatchers) : BaseQueryUseCase<Unit>(dispatchers) {
    override suspend fun execute(param: Unit) = repository.logout()
}
