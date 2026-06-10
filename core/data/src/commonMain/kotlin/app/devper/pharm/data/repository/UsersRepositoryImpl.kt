package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.UsersApi
import app.devper.pharm.data.remote.dto.SetPasswordRequest
import app.devper.pharm.data.remote.dto.SetRoleRequest
import app.devper.pharm.data.remote.dto.SetStatusRequest
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toRequest
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.users.CreateUserParam
import app.devper.pharm.domain.param.users.SetUserPasswordParam
import app.devper.pharm.domain.param.users.SetUserRoleParam
import app.devper.pharm.domain.param.users.SetUserStatusParam
import app.devper.pharm.domain.param.users.UpdateUserParam
import app.devper.pharm.domain.repository.users.UsersRepository

class UsersRepositoryImpl(
    private val api: UsersApi,
) : UsersRepository {

    override suspend fun list(): List<UmUser> =
        api.list().map { it.toDomain() }

    override suspend fun create(param: CreateUserParam): UmUser =
        api.create(param.toRequest()).toDomain()

    override suspend fun update(param: UpdateUserParam): UmUser =
        api.update(param.id, param.toRequest()).toDomain()

    override suspend fun delete(id: String) {
        api.delete(id)
    }

    override suspend fun setRole(param: SetUserRoleParam): UmUser =
        api.setRole(param.id, SetRoleRequest(role = param.role.name)).toDomain()

    override suspend fun setStatus(param: SetUserStatusParam): UmUser =
        api.setStatus(param.id, SetStatusRequest(status = param.status.name)).toDomain()

    override suspend fun setPassword(param: SetUserPasswordParam) {
        api.setPassword(param.id, SetPasswordRequest(password = param.password))
    }
}
