package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.repository.UsersRepository

class GetUsersUseCase(
    private val users: UsersRepository,
    dispatchers: AppDispatchers,
) : BaseQueryUseCase<List<UmUser>>(dispatchers) {
    override suspend fun execute(param: Unit): List<UmUser> = users.list()
}
