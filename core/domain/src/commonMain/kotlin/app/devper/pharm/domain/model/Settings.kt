package app.devper.pharm.domain.model

data class Settings(
    val store: StoreInfo = StoreInfo(),
    val receipt: ReceiptSettings = ReceiptSettings(),
    val stock: StockSettings = StockSettings(),
    val pharmacist: PharmacistInfo = PharmacistInfo(),
    val ky: KySettings = KySettings(),
    val timezone: String = "Asia/Bangkok",
)

data class ReceiptSettings(
    val header: String = "",
    val footer: String = "",
    val paperWidth: String = "58",
    val showPharmacist: Boolean = false,
)

data class StockSettings(
    val lowStockThreshold: Int = 0,
    val reorderDays: Int = 30,
    val reorderLookahead: Int = 14,
    val expiringDays: Int = 60,
)

data class StoreInfo(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val taxId: String = "",
)

data class PharmacistInfo(
    val name: String = "",
    val licenseNo: String = "",
)

data class KySettings(

    val skipAuto: Boolean = false,

    val defaultBuyerAddress: String = "",
)
