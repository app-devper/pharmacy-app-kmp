package app.devper.pharm.domain.usecase.users

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.CreateUserParam
import app.devper.pharm.domain.repository.UsersRepository

class CreateUserUseCase(
    private val users: UsersRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<CreateUserParam, UmUser>(dispatchers) {
    override suspend fun execute(param: CreateUserParam): UmUser = users.create(param)
}
