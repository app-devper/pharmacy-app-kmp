package app.devper.pharm.domain.model

/**
 * A sale kept on this device until the server records it (Sales: Pending
 * sale). [state] says what it is waiting for; see [PendingSaleState].
 */
data class PendingSale(
    val id: String,
    val clientRequestId: String,
    /** The sale request as queued, or the raw stored data of a damaged entry. */
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
    val state: PendingSaleState = PendingSaleState.Pending,
    /** The recorded bill once the server confirmed the sale. */
    val billNo: String? = null,
)

/** What a pending sale is waiting for (KMP ADR-0006, ADR-0010). */
enum class PendingSaleState {
    /** Not yet delivered; synced automatically when the server is reachable. */
    Pending,

    /** The server refused the sale (Sale sync conflict); needs a person. */
    Conflict,

    /** The bill was recorded but the server refused some KY forms. */
    KyPending,

    /** Stored data could not be read; kept raw for export. */
    Damaged,
}
