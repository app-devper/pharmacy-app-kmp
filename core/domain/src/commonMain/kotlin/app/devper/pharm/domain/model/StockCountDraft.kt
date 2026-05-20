package app.devper.pharm.domain.model

data class StockCountDraft(
    val counts: Map<String, String> = emptyMap(),
    val note: String = "",
    val updatedAt: Long = 0L,
) {
    val isEmpty: Boolean get() = counts.isEmpty() && note.isBlank()

    companion object {
        val Empty = StockCountDraft()
    }
}
