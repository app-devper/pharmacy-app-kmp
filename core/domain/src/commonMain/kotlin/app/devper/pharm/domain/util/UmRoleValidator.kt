package app.devper.pharm.domain.util

import app.devper.pharm.domain.model.Role

object UmRoleValidator {
    fun canManage(actor: Role, target: Role, isSelf: Boolean): Boolean {
        if (isSelf) return false
        return when (actor) {
            Role.SUPER -> target == Role.ADMIN || target == Role.MANAGER || target == Role.USER
            Role.ADMIN -> target == Role.MANAGER || target == Role.USER
            Role.MANAGER, Role.USER, Role.UNKNOWN -> false
        }
    }

    fun canManageUsers(actor: Role): Boolean = actor == Role.SUPER || actor == Role.ADMIN

    fun canViewUsers(actor: Role): Boolean = actor == Role.SUPER || actor == Role.ADMIN || actor == Role.MANAGER
}
