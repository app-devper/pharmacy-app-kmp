package app.devper.pharm.domain.model

data class PendingSale(
    val id: String,
    val clientRequestId: String,
    val payloadJson: String,
    val enqueuedAt: Long,
    val refusal: PendingSaleRefusal? = null,
    val attempts: Int = 0,
    val kyForms: List<KyForm> = emptyList(),
    val state: PendingSaleState = PendingSaleState.Pending,
    val billNo: String? = null,
)

enum class PendingSaleState {
    Pending,
    Conflict,
    KyPending,
    Damaged,
}
