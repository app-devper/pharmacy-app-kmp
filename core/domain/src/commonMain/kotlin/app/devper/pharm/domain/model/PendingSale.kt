package app.devper.pharm.domain.model

data class PendingSale(
    val id: String,
    val clientRequestId: String,
    val payloadJson: String,
    val enqueuedAt: Long,
    val lastError: String? = null,
    val attempts: Int = 0,
    /**
     * KY forms to send once the bill is confirmed. A bill the server already
     * recorded may stay queued only for these; replaying it returns the same
     * sale because of its client request id.
     */
    val kyForms: List<KyForm> = emptyList(),
)
