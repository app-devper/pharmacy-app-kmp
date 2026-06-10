package app.devper.pharm.domain.usecase.users

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.users.SetUserStatusParam
import app.devper.pharm.domain.repository.users.UsersRepository

class SetUserStatusUseCase(
    private val users: UsersRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<SetUserStatusParam, UmUser>(dispatchers) {
    override suspend fun execute(param: SetUserStatusParam): UmUser = users.setStatus(param)
}
