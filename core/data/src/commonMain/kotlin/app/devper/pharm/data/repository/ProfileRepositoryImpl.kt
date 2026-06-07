package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.ProfileApi
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toRequest
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.ChangePasswordParam
import app.devper.pharm.domain.param.UpdateProfileParam
import app.devper.pharm.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val api: ProfileApi,
) : ProfileRepository {

    override suspend fun get(): UmUser = api.getMyInfo().toDomain()

    override suspend fun update(param: UpdateProfileParam): UmUser =
        api.updateMyInfo(param.toRequest()).toDomain()

    override suspend fun changePassword(param: ChangePasswordParam) {
        api.changeMyPassword(param.toRequest())
    }
}
