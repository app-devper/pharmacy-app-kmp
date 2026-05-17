package app.devper.pharm.data.storage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PendingSaleDto(
    @SerialName("id") val id: String,
    @SerialName("client_request_id") val clientRequestId: String,
    @SerialName("payload") val payload: String,
    @SerialName("enqueued_at") val enqueuedAt: Long,
    @SerialName("last_error") val lastError: String? = null,
    @SerialName("attempts") val attempts: Int = 0,
)
