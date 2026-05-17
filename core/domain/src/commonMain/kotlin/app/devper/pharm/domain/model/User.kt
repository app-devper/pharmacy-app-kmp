package app.devper.pharm.domain.model

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val role: Role,
)

enum class Role { SUPER, ADMIN, MANAGER, USER, UNKNOWN;
    companion object {
        fun parse(raw: String?): Role = when (raw?.uppercase()) {
            "SUPER"   -> SUPER
            "ADMIN"   -> ADMIN
            "MANAGER" -> MANAGER
            "USER"    -> USER
            else      -> UNKNOWN
        }
    }
}
