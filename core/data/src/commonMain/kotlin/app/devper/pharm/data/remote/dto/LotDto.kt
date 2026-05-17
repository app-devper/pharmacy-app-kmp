package app.devper.pharm.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class DrugLotDto(
    @SerialName("id") val id: String,
    @SerialName("drug_id") val drugId: String,
    @SerialName("drug_name") val drugName: String? = null,
    @SerialName("lot_number") val lotNumber: String,
    @SerialName("expiry_date") val expiryDate: String,
    @SerialName("import_date") val importDate: String,
    @SerialName("cost_price") val costPrice: Double? = null,
    @SerialName("sell_price") val sellPrice: Double? = null,
    @SerialName("quantity") val quantity: Int,
    @SerialName("remaining") val remaining: Int,
)

@Serializable
data class DrugLotInputDto(
    @SerialName("lot_number") val lotNumber: String,
    @SerialName("expiry_date") val expiryDate: String,
    @SerialName("import_date") val importDate: String? = null,
    @SerialName("cost_price") val costPrice: Double? = null,
    @SerialName("sell_price") val sellPrice: Double? = null,
    @SerialName("quantity") val quantity: Int,
)
