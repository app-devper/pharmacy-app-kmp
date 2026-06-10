package app.devper.pharm.domain.param.profile

data class ChangePasswordParam(
    val oldPassword: String,
    val newPassword: String,
)
