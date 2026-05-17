package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.UpdateProfileParam
import app.devper.pharm.domain.repository.ProfileRepository

class UpdateProfileUseCase(
    private val profile: ProfileRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<UpdateProfileParam, UmUser>(dispatchers) {
    override suspend fun execute(param: UpdateProfileParam): UmUser = profile.update(param)
}
