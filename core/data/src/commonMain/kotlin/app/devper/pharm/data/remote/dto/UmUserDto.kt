package app.devper.pharm.data.remote.dto

import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.model.UmUser
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
) {
    fun toDomain(): UmUser = UmUser(
        id = id,
        firstName = firstName,
        lastName = lastName,
        username = username,
        clientId = clientId,
        role = Role.parse(role),
        status = UmStatus.parse(status),
        phone = phone,
        email = email,
        createdDate = createdDate,
        updatedDate = updatedDate,
    )
}
