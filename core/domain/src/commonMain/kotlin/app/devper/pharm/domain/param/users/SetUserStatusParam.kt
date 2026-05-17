package app.devper.pharm.domain.param

import app.devper.pharm.domain.model.UmStatus

data class SetUserStatusParam(
    val id: String,
    val status: UmStatus,
)
