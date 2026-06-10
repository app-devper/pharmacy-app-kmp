package app.devper.pharm.domain.repository.users

import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.users.CreateUserParam
import app.devper.pharm.domain.param.users.SetUserPasswordParam
import app.devper.pharm.domain.param.users.SetUserRoleParam
import app.devper.pharm.domain.param.users.SetUserStatusParam
import app.devper.pharm.domain.param.users.UpdateUserParam

interface UsersRepository {
    suspend fun list(): List<UmUser>
    suspend fun create(param: CreateUserParam): UmUser
    suspend fun update(param: UpdateUserParam): UmUser
    suspend fun delete(id: String)
    suspend fun setRole(param: SetUserRoleParam): UmUser
    suspend fun setStatus(param: SetUserStatusParam): UmUser
    suspend fun setPassword(param: SetUserPasswordParam)
}
