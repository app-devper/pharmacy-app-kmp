package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.Role

fun Role.canManage(target: Role, isSelf: Boolean): Boolean {
    if (isSelf) return false
    return when (this) {
        Role.SUPER -> target == Role.ADMIN || target == Role.MANAGER || target == Role.USER
        Role.ADMIN -> target == Role.MANAGER || target == Role.USER
        Role.MANAGER, Role.USER, Role.UNKNOWN -> false
    }
}

fun Role.canManageUsers(): Boolean = this == Role.SUPER || this == Role.ADMIN

fun Role.canViewUsers(): Boolean = this == Role.SUPER || this == Role.ADMIN || this == Role.MANAGER
