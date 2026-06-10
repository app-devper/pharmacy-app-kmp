package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.LoginRequest
import app.devper.pharm.domain.param.auth.LoginParam

internal fun LoginParam.toRequest(): LoginRequest = LoginRequest(
    username = username,
    password = password,
    system = system,
)
