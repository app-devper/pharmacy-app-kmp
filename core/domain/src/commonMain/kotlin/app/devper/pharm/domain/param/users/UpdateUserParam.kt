package app.devper.pharm.domain.param.users

data class UpdateUserParam(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
)
