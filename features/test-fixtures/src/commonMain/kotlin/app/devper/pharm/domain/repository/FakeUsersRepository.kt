package app.devper.pharm.domain.repository

import app.devper.pharm.domain.repository.users.UsersRepository

import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.users.CreateUserParam
import app.devper.pharm.domain.param.users.SetUserPasswordParam
import app.devper.pharm.domain.param.users.SetUserRoleParam
import app.devper.pharm.domain.param.users.SetUserStatusParam
import app.devper.pharm.domain.param.users.UpdateUserParam

class FakeUsersRepository(
    initial: List<UmUser> = sampleUsers,
    private val listFailsWith: Throwable? = null,
    private val createFailsWith: Throwable? = null,
    private val updateFailsWith: Throwable? = null,
    private val deleteFailsWith: Throwable? = null,
    private val setRoleFailsWith: Throwable? = null,
    private val setStatusFailsWith: Throwable? = null,
    private val setPasswordFailsWith: Throwable? = null,
) : UsersRepository {

    private val data: MutableList<UmUser> = initial.toMutableList()
    var lastCreate: CreateUserParam? = null
        private set
    var lastUpdate: UpdateUserParam? = null
        private set
    var lastDelete: String? = null
        private set
    var lastSetRole: SetUserRoleParam? = null
        private set
    var lastSetStatus: SetUserStatusParam? = null
        private set
    var lastSetPassword: SetUserPasswordParam? = null
        private set

    val snapshot: List<UmUser> get() = data.toList()

    override suspend fun list(): List<UmUser> {
        listFailsWith?.let { throw it }
        return data.toList()
    }

    override suspend fun create(param: CreateUserParam): UmUser {
        createFailsWith?.let { throw it }
        lastCreate = param
        val newUser = UmUser(
            id = "u-${data.size + 1}",
            firstName = param.firstName,
            lastName = param.lastName,
            username = param.username,
            clientId = param.clientId,
            role = Role.USER,
            status = UmStatus.ACTIVE,
            phone = param.phone,
            email = param.email,
            createdDate = null,
            updatedDate = null,
        )
        data.add(newUser)
        return newUser
    }

    override suspend fun update(param: UpdateUserParam): UmUser {
        updateFailsWith?.let { throw it }
        lastUpdate = param
        val idx = data.indexOfFirst { it.id == param.id }
        if (idx < 0) throw NoSuchElementException("user ${param.id} not found")
        val updated = data[idx].copy(
            firstName = param.firstName,
            lastName = param.lastName,
            phone = param.phone,
            email = param.email,
        )
        data[idx] = updated
        return updated
    }

    override suspend fun delete(id: String) {
        deleteFailsWith?.let { throw it }
        lastDelete = id
        data.removeAll { it.id == id }
    }

    override suspend fun setRole(param: SetUserRoleParam): UmUser {
        setRoleFailsWith?.let { throw it }
        lastSetRole = param
        val idx = data.indexOfFirst { it.id == param.id }
        if (idx < 0) throw NoSuchElementException("user ${param.id} not found")
        val updated = data[idx].copy(role = param.role)
        data[idx] = updated
        return updated
    }

    override suspend fun setStatus(param: SetUserStatusParam): UmUser {
        setStatusFailsWith?.let { throw it }
        lastSetStatus = param
        val idx = data.indexOfFirst { it.id == param.id }
        if (idx < 0) throw NoSuchElementException("user ${param.id} not found")
        val updated = data[idx].copy(status = param.status)
        data[idx] = updated
        return updated
    }

    override suspend fun setPassword(param: SetUserPasswordParam) {
        setPasswordFailsWith?.let { throw it }
        lastSetPassword = param
    }

    companion object {
        val sampleUsers: List<UmUser> = listOf(
            UmUser(
                id = "u-me", firstName = "ฉัน", lastName = "เอง", username = "selfadmin",
                clientId = "PHA", role = Role.SUPER, status = UmStatus.ACTIVE,
                phone = "0810000000", email = "me@example.com",
                createdDate = null, updatedDate = null,
            ),
            UmUser(
                id = "u-1", firstName = "สมชาย", lastName = "ใจดี", username = "somchai",
                clientId = "PHA", role = Role.ADMIN, status = UmStatus.ACTIVE,
                phone = "0812345678", email = "somchai@example.com",
                createdDate = null, updatedDate = null,
            ),
            UmUser(
                id = "u-2", firstName = "สมหญิง", lastName = "พริ้งพราย", username = "somying",
                clientId = "PHA", role = Role.USER, status = UmStatus.ACTIVE,
                phone = "0898765432", email = "somying@example.com",
                createdDate = null, updatedDate = null,
            ),
        )
    }
}
