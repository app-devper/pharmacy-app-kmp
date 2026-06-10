package app.devper.pharm.domain.param.settings

import app.devper.pharm.domain.model.KySettings
import app.devper.pharm.domain.model.PharmacistInfo
import app.devper.pharm.domain.model.StoreInfo

data class UpdateSettingsParam(
    val store: StoreInfo,
    val receipt: ReceiptSettingsInput,
    val stock: StockSettingsInput,
    val pharmacist: PharmacistInfo,
    val ky: KySettings,
    val timezone: String,
)

data class ReceiptSettingsInput(
    val header: String = "",
    val footer: String = "",
    val paperWidth: String = "58",
    val showPharmacist: Boolean = false,
)

data class StockSettingsInput(
    val lowStockThreshold: Int = 0,
    val reorderDays: Int = 30,
    val reorderLookahead: Int = 14,
    val expiringDays: Int = 60,
)
