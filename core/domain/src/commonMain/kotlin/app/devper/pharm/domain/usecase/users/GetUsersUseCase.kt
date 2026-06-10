package app.devper.pharm.domain.usecase.users

import app.devper.pharm.domain.usecase.BaseQueryUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.repository.users.UsersRepository

class GetUsersUseCase(
    private val users: UsersRepository,
    dispatchers: AppDispatchers,
) : BaseQueryUseCase<List<UmUser>>(dispatchers) {
    override suspend fun execute(param: Unit): List<UmUser> = users.list()
}
