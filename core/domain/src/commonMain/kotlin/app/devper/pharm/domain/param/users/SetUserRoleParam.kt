package app.devper.pharm.domain.param.users

import app.devper.pharm.domain.model.Role

data class SetUserRoleParam(
    val id: String,
    val role: Role,
)
