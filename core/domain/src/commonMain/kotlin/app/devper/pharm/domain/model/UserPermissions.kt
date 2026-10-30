package app.devper.pharm.domain.model

data class UserPermissions(
    val edit: Boolean = false,
    val delete: Boolean = false,
    val setStatus: Boolean = false,
    val setRole: Boolean = false,
    val setPassword: Boolean = false,
    val unlock: Boolean = false,
    val assignableRoles: List<Role> = emptyList(),
) {
    val any: Boolean get() = edit || delete || setStatus || setRole || setPassword

    companion object {
        val None = UserPermissions()
    }
}

data class UserRules(val creatableRoles: List<Role> = emptyList())
