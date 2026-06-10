package app.devper.pharm.domain.param.profile

data class UpdateProfileParam(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
)
