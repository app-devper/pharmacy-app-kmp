package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomerDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("price_tier") val priceTier: String? = null,
    @SerialName("disease") val disease: String? = null,
)

@Serializable
data class CustomerInputDto(
    @SerialName("name") val name: String,
    @SerialName("phone") val phone: String = "",
    @SerialName("disease") val disease: String = "",
    @SerialName("price_tier") val priceTier: String = "",
)
