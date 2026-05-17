package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockCountDto(
    @SerialName("id") val id: String,
    @SerialName("count_no") val countNo: String = "",
    @SerialName("note") val note: String = "",
    @SerialName("items") val items: List<StockCountItemDto> = emptyList(),
    @SerialName("created_at") val createdAt: String = "",
)

@Serializable
data class StockCountItemDto(
    @SerialName("drug_id") val drugId: String,
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("unit") val unit: String = "",
    @SerialName("system_stock") val systemStock: Int = 0,
    @SerialName("counted") val counted: Int = 0,
    @SerialName("delta") val delta: Int = 0,
)

@Serializable
data class StockCountInputDto(
    @SerialName("note") val note: String = "",
    @SerialName("items") val items: List<StockCountInputItemDto>,
)

@Serializable
data class StockCountInputItemDto(
    @SerialName("drug_id") val drugId: String,
    @SerialName("counted") val counted: Int,
)
