package app.devper.pharm.domain.usecase.profile

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.profile.UpdateProfileParam
import app.devper.pharm.domain.repository.profile.ProfileRepository

class UpdateProfileUseCase(
    private val profile: ProfileRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<UpdateProfileParam, UmUser>(dispatchers) {
    override suspend fun execute(param: UpdateProfileParam): UmUser = profile.update(param)
}
