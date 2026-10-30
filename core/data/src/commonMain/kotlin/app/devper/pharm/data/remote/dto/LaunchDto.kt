package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class TicketRequest(
    @SerialName("ticket") val ticket: String,
)

@Serializable
data class SystemResponse(
    @SerialName("systemCode") val systemCode: String,
    @SerialName("clientId") val clientId: String? = null,
    @SerialName("host") val host: String? = null,
)

@Serializable
data class PasskeyLoginOptionsRequest(
    @SerialName("system") val system: String,
)

@Serializable
data class PasskeyOptionsResponse(
    @SerialName("ceremonyId") val ceremonyId: String,
    @SerialName("options") val options: JsonElement,
)

@Serializable
data class PasskeyLoginRequest(
    @SerialName("ceremonyId") val ceremonyId: String,
    @SerialName("credential") val credential: JsonElement,
    @SerialName("username") val username: String? = null,
)

@Serializable
data class UmErrorResponse(
    @SerialName("code") val code: String? = null,
    @SerialName("message") val message: String? = null,
)
