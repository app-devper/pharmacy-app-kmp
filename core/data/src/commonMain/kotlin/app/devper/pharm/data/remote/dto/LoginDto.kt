package app.devper.pharm.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LoginRequest(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,
    @SerialName("system") val system: String,
)

@Serializable
data class LoginResponse(
    @SerialName("accessToken") val accessToken: String,
)

@Serializable
data class UserInfoResponse(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("role") val role: String,
    @SerialName("email") val email: String? = null,
    @SerialName("phone") val phone: String? = null,
)
