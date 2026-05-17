package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,
    @SerialName("phone") val phone: String,
    @SerialName("email") val email: String,
    @SerialName("clientId") val clientId: String,
)
