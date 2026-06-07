package app.devper.pharm.domain.repository

import app.devper.pharm.common.AuthException
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.ChangePasswordParam
import app.devper.pharm.domain.param.UpdateProfileParam

class FakeProfileRepository(
    initial: UmUser = sampleUser,
    private val getFailsWith: Throwable? = null,
    private val updateFailsWith: Throwable? = null,
    private val changePasswordFailsWith: Throwable? = null,
    private val expectedOldPassword: String? = null,
) : ProfileRepository {

    private var current: UmUser = initial
    var lastUpdate: UpdateProfileParam? = null
        private set
    var lastChangePassword: ChangePasswordParam? = null
        private set

    val snapshot: UmUser get() = current

    override suspend fun get(): UmUser {
        getFailsWith?.let { throw it }
        return current
    }

    override suspend fun update(param: UpdateProfileParam): UmUser {
        updateFailsWith?.let { throw it }
        lastUpdate = param
        current = current.copy(
            firstName = param.firstName,
            lastName = param.lastName,
            phone = param.phone,
            email = param.email,
        )
        return current
    }

    override suspend fun changePassword(param: ChangePasswordParam) {
        changePasswordFailsWith?.let { throw it }
        if (expectedOldPassword != null && param.oldPassword != expectedOldPassword) {
            throw AuthException("รหัสผ่านเดิมไม่ถูกต้อง")
        }
        lastChangePassword = param
    }

    companion object {
        val sampleUser = UmUser(
            id = "u-1",
            firstName = "สมชาย",
            lastName = "ใจดี",
            username = "somchai",
            clientId = "PHA",
            role = Role.ADMIN,
            status = UmStatus.ACTIVE,
            phone = "0812345678",
            email = "somchai@example.com",
            createdDate = kotlinx.datetime.LocalDateTime.parse("2025-01-01T00:00:00"),
            updatedDate = kotlinx.datetime.LocalDateTime.parse("2025-01-01T00:00:00"),
        )
        val notFound = NotFoundException("ไม่พบผู้ใช้")
    }
}
