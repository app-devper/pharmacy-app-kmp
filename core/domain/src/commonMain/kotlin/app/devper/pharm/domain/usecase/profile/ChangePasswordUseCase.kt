package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.ChangePasswordParam
import app.devper.pharm.domain.repository.ProfileRepository

class ChangePasswordUseCase(
    private val profile: ProfileRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<ChangePasswordParam, Unit>(dispatchers) {
    override suspend fun execute(param: ChangePasswordParam) = profile.changePassword(param)
}
