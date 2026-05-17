package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.CreateUserParam
import app.devper.pharm.domain.param.SetUserPasswordParam
import app.devper.pharm.domain.param.SetUserRoleParam
import app.devper.pharm.domain.param.SetUserStatusParam
import app.devper.pharm.domain.param.UpdateUserParam

interface UsersRepository {
    suspend fun list(): List<UmUser>
    suspend fun create(param: CreateUserParam): UmUser
    suspend fun update(param: UpdateUserParam): UmUser
    suspend fun delete(id: String)
    suspend fun setRole(param: SetUserRoleParam): UmUser
    suspend fun setStatus(param: SetUserStatusParam): UmUser
    suspend fun setPassword(param: SetUserPasswordParam)
}
