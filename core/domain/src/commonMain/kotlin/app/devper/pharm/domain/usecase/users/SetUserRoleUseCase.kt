package app.devper.pharm.domain.usecase.users

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.users.SetUserRoleParam
import app.devper.pharm.domain.repository.users.UsersRepository

class SetUserRoleUseCase(
    private val users: UsersRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<SetUserRoleParam, UmUser>(dispatchers) {
    override suspend fun execute(param: SetUserRoleParam): UmUser = users.setRole(param)
}
