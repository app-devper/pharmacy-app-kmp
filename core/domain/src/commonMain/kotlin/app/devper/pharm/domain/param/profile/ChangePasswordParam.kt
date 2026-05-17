package app.devper.pharm.domain.param

data class ChangePasswordParam(
    val oldPassword: String,
    val newPassword: String,
)
