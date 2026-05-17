package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupplierDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("contact_name") val contactName: String = "",
    @SerialName("phone") val phone: String = "",
    @SerialName("address") val address: String = "",
    @SerialName("tax_id") val taxId: String = "",
    @SerialName("notes") val notes: String = "",
)

@Serializable
data class SupplierInputDto(
    @SerialName("name") val name: String,
    @SerialName("contact_name") val contactName: String = "",
    @SerialName("phone") val phone: String = "",
    @SerialName("address") val address: String = "",
    @SerialName("tax_id") val taxId: String = "",
    @SerialName("notes") val notes: String = "",
)
