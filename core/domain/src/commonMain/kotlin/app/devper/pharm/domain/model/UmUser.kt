package app.devper.pharm.domain.model

import kotlinx.datetime.LocalDateTime

data class UmUser(
    val id: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val clientId: String,
    val role: Role,
    val status: UmStatus,
    val phone: String,
    val email: String,
    val createdDate: LocalDateTime?,
    val updatedDate: LocalDateTime?,
) {
    val displayName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ").ifBlank { username }

    val initials: String
        get() = listOfNotNull(
            firstName.firstOrNull()?.uppercaseChar(),
            lastName.firstOrNull()?.uppercaseChar(),
        ).joinToString("").ifBlank { username.take(2).uppercase() }
}

enum class UmStatus { ACTIVE, INACTIVE, UNKNOWN;
    val isActive: Boolean get() = this == ACTIVE

    companion object {
        fun parse(raw: String?): UmStatus = when (raw?.uppercase()) {
            "ACTIVE"   -> ACTIVE
            "INACTIVE" -> INACTIVE
            else       -> UNKNOWN
        }
    }
}
