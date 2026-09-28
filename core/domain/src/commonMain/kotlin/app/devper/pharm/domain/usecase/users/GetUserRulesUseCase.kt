package app.devper.pharm.domain.usecase.users

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.UserRules
import app.devper.pharm.domain.repository.users.UsersRepository
import app.devper.pharm.domain.usecase.BaseQueryUseCase

/** What the signed-in user may do beyond individual users, such as creating users. */
class GetUserRulesUseCase(
    private val users: UsersRepository,
    dispatchers: AppDispatchers,
) : BaseQueryUseCase<UserRules>(dispatchers) {
    override suspend fun execute(param: Unit): UserRules = users.rules()
}
