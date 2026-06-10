package app.devper.pharm.domain.usecase.users

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.UpdateUserParam
import app.devper.pharm.domain.repository.UsersRepository

class UpdateUserUseCase(
    private val users: UsersRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<UpdateUserParam, UmUser>(dispatchers) {
    override suspend fun execute(param: UpdateUserParam): UmUser = users.update(param)
}
