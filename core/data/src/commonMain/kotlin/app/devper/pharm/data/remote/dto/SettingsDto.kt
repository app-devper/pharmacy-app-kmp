package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SettingsDto(
    @SerialName("store") val store: StoreInfoDto = StoreInfoDto(),
    @SerialName("receipt") val receipt: ReceiptSettingsDto = ReceiptSettingsDto(),
    @SerialName("stock") val stock: StockSettingsDto = StockSettingsDto(),
    @SerialName("pharmacist") val pharmacist: PharmacistInfoDto = PharmacistInfoDto(),
    @SerialName("ky") val ky: KySettingsDto = KySettingsDto(),
    @SerialName("timezone") val timezone: String = "",
)

@Serializable
data class StoreInfoDto(
    @SerialName("name") val name: String = "",
    @SerialName("address") val address: String = "",
    @SerialName("phone") val phone: String = "",
    @SerialName("tax_id") val taxId: String = "",
)

@Serializable
data class ReceiptSettingsDto(
    @SerialName("header") val header: String = "",
    @SerialName("footer") val footer: String = "",
    @SerialName("paper_width") val paperWidth: String = "58",
    @SerialName("show_pharmacist") val showPharmacist: Boolean = false,
)

@Serializable
data class StockSettingsDto(
    @SerialName("low_stock_threshold") val lowStockThreshold: Int = 0,
    @SerialName("reorder_days") val reorderDays: Int = 30,
    @SerialName("reorder_lookahead") val reorderLookahead: Int = 14,
    @SerialName("expiring_days") val expiringDays: Int = 60,
)

@Serializable
data class PharmacistInfoDto(
    @SerialName("name") val name: String = "",
    @SerialName("license_no") val licenseNo: String = "",
)

@Serializable
data class KySettingsDto(
    @SerialName("skip_auto") val skipAuto: Boolean = false,
    @SerialName("default_buyer_address") val defaultBuyerAddress: String = "",
)

@Serializable
data class SettingsInputDto(
    @SerialName("store") val store: StoreInfoDto,
    @SerialName("receipt") val receipt: ReceiptSettingsDto,
    @SerialName("stock") val stock: StockSettingsDto,
    @SerialName("pharmacist") val pharmacist: PharmacistInfoDto,
    @SerialName("ky") val ky: KySettingsDto,
    @SerialName("timezone") val timezone: String,
)
