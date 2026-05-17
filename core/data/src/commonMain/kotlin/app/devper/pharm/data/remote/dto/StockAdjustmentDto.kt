package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockAdjustmentDto(
    @SerialName("id") val id: String,
    @SerialName("drug_id") val drugId: String,
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("delta") val delta: Int = 0,
    @SerialName("before") val before: Int = 0,
    @SerialName("after") val after: Int = 0,
    @SerialName("reason") val reason: String = "",
    @SerialName("note") val note: String = "",
    @SerialName("created_at") val createdAt: String = "",
)

@Serializable
data class StockAdjustmentInputDto(
    @SerialName("delta") val delta: Int,
    @SerialName("reason") val reason: String,
    @SerialName("note") val note: String = "",
)
