package app.devper.pharm.data.storage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockCountDraftDto(
    @SerialName("v") val version: Int = SCHEMA_VERSION,
    @SerialName("counts") val counts: Map<String, String> = emptyMap(),
    @SerialName("note") val note: String = "",
    @SerialName("updated_at") val updatedAt: Long = 0L,
) {
    companion object {
        const val SCHEMA_VERSION = 1
    }
}
