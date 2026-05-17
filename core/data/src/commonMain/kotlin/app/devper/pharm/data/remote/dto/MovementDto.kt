package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovementDto(
    @SerialName("id") val id: String,
    @SerialName("type") val type: String,
    @SerialName("drug_id") val drugId: String = "",
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("delta") val delta: Int = 0,
    @SerialName("reference") val reference: String = "",
    @SerialName("note") val note: String = "",
    @SerialName("at") val at: String = "",
)

@Serializable
data class MovementsPageDto(
    @SerialName("total") val total: Int = 0,
    @SerialName("items") val items: List<MovementDto> = emptyList(),
)
