package app.devper.pharm.data.storage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParkedCartDto(
    @SerialName("v") val version: Int = SCHEMA_VERSION,
    @SerialName("items") val items: List<ParkedCartLineDto>,
    @SerialName("customer") val customer: ParkedCustomerDto? = null,
    @SerialName("cart_discount") val cartDiscount: ParkedDiscountDto = ParkedDiscountDto(),
    @SerialName("active_tier") val activeTier: String,
    @SerialName("cash_received") val cashReceived: String = "",
    @SerialName("parked_at") val parkedAt: Long,
) {
    companion object {

        const val SCHEMA_VERSION = 1
    }
}

@Serializable
data class ParkedCartLineDto(
    @SerialName("drug") val drug: ParkedDrugDto,
    @SerialName("qty") val qty: Int,
    @SerialName("tier") val tier: String,
    @SerialName("discount") val discount: Double = 0.0,
    @SerialName("selected_unit") val selectedUnit: ParkedAltUnitDto? = null,
)

@Serializable
data class ParkedDrugDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("generic_name") val genericName: String? = null,
    @SerialName("barcode") val barcode: String? = null,
    @SerialName("reg_no") val regNo: String? = null,
    @SerialName("unit") val unit: String? = null,
    @SerialName("sell_price") val sellPrice: Double,
    @SerialName("stock") val stock: Int,
    @SerialName("prices") val prices: Map<String, Double> = emptyMap(),
    @SerialName("alt_units") val altUnits: List<ParkedAltUnitDto> = emptyList(),
    @SerialName("report_types") val reportTypes: List<String> = emptyList(),
)

@Serializable
data class ParkedAltUnitDto(
    @SerialName("name") val name: String,
    @SerialName("factor") val factor: Int,
    @SerialName("sell_price") val sellPrice: Double,
    @SerialName("prices") val prices: Map<String, Double> = emptyMap(),
    @SerialName("barcode") val barcode: String? = null,
    @SerialName("hidden") val hidden: Boolean = false,
)

@Serializable
data class ParkedCustomerDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("price_tier") val priceTier: String,
    @SerialName("allergy_note") val allergyNote: String? = null,
)

@Serializable
data class ParkedDiscountDto(

    @SerialName("kind") val kind: String = "none",
    @SerialName("value") val value: Double = 0.0,
)
