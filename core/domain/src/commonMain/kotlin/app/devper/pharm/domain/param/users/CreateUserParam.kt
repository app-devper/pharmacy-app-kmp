package app.devper.pharm.domain.param.users

data class CreateUserParam(
    val firstName: String,
    val lastName: String,
    val username: String,
    val password: String,
    val phone: String,
    val email: String,
    val clientId: String = "PHA",
)
