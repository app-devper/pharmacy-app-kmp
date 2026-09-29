package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.Role

/**
 * Shared role policy (ADR-0004): USER < MANAGER < ADMIN < SUPER. pharmacy-api
 * enforces the same order per route; UNKNOWN meets no minimum.
 */
fun Role.atLeast(min: Role): Boolean = rank() > 0 && rank() >= min.rank()

private fun Role.rank(): Int = when (this) {
    Role.USER -> 1
    Role.MANAGER -> 2
    Role.ADMIN -> 3
    Role.SUPER -> 4
    Role.UNKNOWN -> 0
}

fun Role.canViewUsers(): Boolean = this == Role.SUPER || this == Role.ADMIN || this == Role.MANAGER
