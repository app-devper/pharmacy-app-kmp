package app.devper.pharm.domain.usecase.profile

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.profile.ChangePasswordParam
import app.devper.pharm.domain.repository.profile.ProfileRepository

class ChangePasswordUseCase(
    private val profile: ProfileRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<ChangePasswordParam, Unit>(dispatchers) {
    override suspend fun execute(param: ChangePasswordParam) = profile.changePassword(param)
}
