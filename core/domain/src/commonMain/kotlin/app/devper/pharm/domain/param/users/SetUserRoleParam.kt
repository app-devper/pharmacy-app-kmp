package app.devper.pharm.domain.param

import app.devper.pharm.domain.model.Role

data class SetUserRoleParam(
    val id: String,
    val role: Role,
)
