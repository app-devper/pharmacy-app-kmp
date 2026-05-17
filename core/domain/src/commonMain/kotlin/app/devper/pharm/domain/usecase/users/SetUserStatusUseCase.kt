package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.SetUserStatusParam
import app.devper.pharm.domain.repository.UsersRepository

class SetUserStatusUseCase(
    private val users: UsersRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<SetUserStatusParam, UmUser>(dispatchers) {
    override suspend fun execute(param: SetUserStatusParam): UmUser = users.setStatus(param)
}
