package app.devper.pharm.domain.repository.profile

import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.profile.ChangePasswordParam
import app.devper.pharm.domain.param.profile.UpdateProfileParam

interface ProfileRepository {
    suspend fun get(): UmUser
    suspend fun update(param: UpdateProfileParam): UmUser
    suspend fun changePassword(param: ChangePasswordParam)
}
