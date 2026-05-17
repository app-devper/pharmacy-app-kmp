package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.SetUserPasswordParam
import app.devper.pharm.domain.repository.UsersRepository

class SetUserPasswordUseCase(
    private val users: UsersRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<SetUserPasswordParam, Unit>(dispatchers) {
    override suspend fun execute(param: SetUserPasswordParam) = users.setPassword(param)
}
