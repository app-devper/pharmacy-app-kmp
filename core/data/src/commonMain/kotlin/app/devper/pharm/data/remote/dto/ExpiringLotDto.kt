package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExpiringLotDto(
    @SerialName("id") val id: String,
    @SerialName("drug_id") val drugId: String,
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("lot_number") val lotNumber: String = "",
    @SerialName("expiry_date") val expiryDate: String = "",
    @SerialName("remaining") val remaining: Int = 0,
    @SerialName("days_left") val daysLeft: Int = 0,
)

@Serializable
data class WriteoffLotsInputDto(
    @SerialName("lot_ids") val lotIds: List<String>,
)

@Serializable
data class WriteoffResultDto(
    @SerialName("written_off") val writtenOff: Int = 0,
    @SerialName("failed") val failed: List<WriteoffFailureDto> = emptyList(),
)

@Serializable
data class WriteoffFailureDto(
    @SerialName("lot_id") val lotId: String = "",
    @SerialName("error") val error: String = "",
)
