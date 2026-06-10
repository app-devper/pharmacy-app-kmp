package app.devper.pharm.domain.usecase.users

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.users.SetUserPasswordParam
import app.devper.pharm.domain.repository.users.UsersRepository

class SetUserPasswordUseCase(
    private val users: UsersRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<SetUserPasswordParam, Unit>(dispatchers) {
    override suspend fun execute(param: SetUserPasswordParam) = users.setPassword(param)
}
