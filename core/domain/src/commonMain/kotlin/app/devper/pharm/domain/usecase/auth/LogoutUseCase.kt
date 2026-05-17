package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository, dispatchers: AppDispatchers) : BaseUseCase<Unit, Unit>(dispatchers) {
    override suspend fun execute(param: Unit) = repository.logout()
    suspend operator fun invoke(): Result<Unit> = invoke(Unit)
}
