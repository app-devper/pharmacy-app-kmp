package app.devper.pharm.domain.model

/** What the signed-in user may do to one user, as UM decides it (um-api ADR-0006). */
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

/** What the signed-in user may do beyond individual users. */
data class UserRules(val creatableRoles: List<Role> = emptyList())
