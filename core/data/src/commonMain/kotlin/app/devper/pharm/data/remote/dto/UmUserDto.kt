package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UmUserDto(
    @SerialName("id") val id: String,
    @SerialName("firstName") val firstName: String = "",
    @SerialName("lastName") val lastName: String = "",
    @SerialName("username") val username: String,
    @SerialName("clientId") val clientId: String = "",
    @SerialName("role") val role: String = "",
    @SerialName("status") val status: String = "",
    @SerialName("phone") val phone: String = "",
    @SerialName("email") val email: String = "",
    @SerialName("createdDate") val createdDate: String = "",
    @SerialName("updatedDate") val updatedDate: String = "",
    @SerialName("can") val can: UserPermissionsDto? = null,
)

/** um-api ADR-0006. */
@Serializable
data class UserPermissionsDto(
    @SerialName("edit") val edit: Boolean = false,
    @SerialName("delete") val delete: Boolean = false,
    @SerialName("setStatus") val setStatus: Boolean = false,
    @SerialName("setRole") val setRole: Boolean = false,
    @SerialName("setPassword") val setPassword: Boolean = false,
    @SerialName("unlock") val unlock: Boolean = false,
    @SerialName("assignableRoles") val assignableRoles: List<String> = emptyList(),
)

@Serializable
data class UserRulesDto(
    @SerialName("creatableRoles") val creatableRoles: List<String> = emptyList(),
)
