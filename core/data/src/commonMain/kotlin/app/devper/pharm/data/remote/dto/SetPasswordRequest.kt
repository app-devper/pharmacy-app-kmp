package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetPasswordRequest(
    @SerialName("password") val password: String,
)
