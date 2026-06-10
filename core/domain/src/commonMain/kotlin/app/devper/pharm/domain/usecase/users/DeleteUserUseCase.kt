package app.devper.pharm.domain.usecase.users

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.repository.UsersRepository

class DeleteUserUseCase(
    private val users: UsersRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<String, Unit>(dispatchers) {
    override suspend fun execute(param: String) = users.delete(param)
}
