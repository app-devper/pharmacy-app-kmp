package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LabelLineRequest(
    @SerialName("drug_name") val drugName: String,
    @SerialName("lot_number") val lotNumber: String = "",
    @SerialName("barcode") val barcode: String,
    @SerialName("price") val price: Double = 0.0,
    @SerialName("include_price") val includePrice: Boolean,
    @SerialName("copies") val copies: Int,
)

@Serializable
data class PrintLabelsRequest(
    @SerialName("size") val size: String,
    @SerialName("lines") val lines: List<LabelLineRequest>,
)
