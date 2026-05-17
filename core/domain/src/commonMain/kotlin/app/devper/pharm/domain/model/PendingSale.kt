package app.devper.pharm.domain.model

data class PendingSale(
    val id: String,
    val clientRequestId: String,
    val payloadJson: String,
    val enqueuedAt: Long,
    val lastError: String? = null,
    val attempts: Int = 0,
)
