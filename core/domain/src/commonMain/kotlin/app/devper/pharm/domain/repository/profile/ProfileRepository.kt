package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.ChangePasswordParam
import app.devper.pharm.domain.param.UpdateProfileParam

interface ProfileRepository {
    suspend fun get(): UmUser
    suspend fun update(param: UpdateProfileParam): UmUser
    suspend fun changePassword(param: ChangePasswordParam)
}
