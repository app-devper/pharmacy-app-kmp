package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.internal.parseLocalDateTimeOrNull
import app.devper.pharm.data.remote.dto.UmUserDto
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.model.UmUser

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
