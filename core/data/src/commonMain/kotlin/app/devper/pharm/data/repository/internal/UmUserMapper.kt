package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.internal.parseLocalDateTimeOrNull
import app.devper.pharm.data.remote.dto.ChangePasswordRequest
import app.devper.pharm.data.remote.dto.CreateUserRequest
import app.devper.pharm.data.remote.dto.UmUserDto
import app.devper.pharm.data.remote.dto.UpdateProfileRequest
import app.devper.pharm.data.remote.dto.UpdateUserRequest
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.param.profile.ChangePasswordParam
import app.devper.pharm.domain.param.users.CreateUserParam
import app.devper.pharm.domain.param.profile.UpdateProfileParam
import app.devper.pharm.domain.param.users.UpdateUserParam

internal fun UmUserDto.toDomain(): UmUser = UmUser(
    id = id,
    firstName = firstName,
    lastName = lastName,
    username = username,
    clientId = clientId,
    role = Role.parse(role),
    status = UmStatus.parse(status),
    phone = phone,
    email = email,
    createdDate = createdDate.parseLocalDateTimeOrNull(),
    updatedDate = updatedDate.parseLocalDateTimeOrNull(),
)

internal fun CreateUserParam.toRequest(): CreateUserRequest = CreateUserRequest(
    firstName = firstName.trim(),
    lastName = lastName.trim(),
    username = username.trim(),
    password = password,
    phone = phone.trim(),
    email = email.trim(),
    clientId = clientId,
)

internal fun UpdateUserParam.toRequest(): UpdateUserRequest = UpdateUserRequest(
    firstName = firstName.trim(),
    lastName = lastName.trim(),
    phone = phone.trim(),
    email = email.trim(),
)

internal fun UpdateProfileParam.toRequest(): UpdateProfileRequest = UpdateProfileRequest(
    firstName = firstName.trim(),
    lastName = lastName.trim(),
    phone = phone.trim(),
    email = email.trim(),
)

internal fun ChangePasswordParam.toRequest(): ChangePasswordRequest = ChangePasswordRequest(
    oldPassword = oldPassword,
    newPassword = newPassword,
)
