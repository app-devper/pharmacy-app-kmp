package app.devper.pharm.domain.usecase.profile

import app.devper.pharm.domain.usecase.BaseQueryUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.repository.ProfileRepository

class GetProfileUseCase(
    private val profile: ProfileRepository,
    dispatchers: AppDispatchers,
) : BaseQueryUseCase<UmUser>(dispatchers) {
    override suspend fun execute(param: Unit): UmUser = profile.get()
}
