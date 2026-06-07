package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.KySettingsDto
import app.devper.pharm.data.remote.dto.PharmacistInfoDto
import app.devper.pharm.data.remote.dto.ReceiptSettingsDto
import app.devper.pharm.data.remote.dto.SettingsDto
import app.devper.pharm.data.remote.dto.StockSettingsDto
import app.devper.pharm.data.remote.dto.StoreInfoDto
import app.devper.pharm.domain.model.KySettings
import app.devper.pharm.domain.model.PharmacistInfo
import app.devper.pharm.domain.model.ReceiptSettings
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.model.StockSettings
import app.devper.pharm.domain.model.StoreInfo

internal fun SettingsDto.toDomain(): Settings = Settings(
    store = store.toDomain(),
    receipt = receipt.toDomain(),
    stock = stock.toDomain(),
    pharmacist = pharmacist.toDomain(),
    ky = ky.toDomain(),
    timezone = timezone.takeIf { it.isNotBlank() } ?: "Asia/Bangkok",
)

internal fun StoreInfoDto.toDomain(): StoreInfo = StoreInfo(
    name = name, address = address, phone = phone, taxId = taxId,
)

internal fun ReceiptSettingsDto.toDomain(): ReceiptSettings = ReceiptSettings(
    header = header, footer = footer, paperWidth = paperWidth, showPharmacist = showPharmacist,
)

internal fun StockSettingsDto.toDomain(): StockSettings = StockSettings(
    lowStockThreshold = lowStockThreshold,
    reorderDays = reorderDays,
    reorderLookahead = reorderLookahead,
    expiringDays = expiringDays,
)

internal fun PharmacistInfoDto.toDomain(): PharmacistInfo = PharmacistInfo(
    name = name, licenseNo = licenseNo,
)

internal fun KySettingsDto.toDomain(): KySettings = KySettings(
    skipAuto = skipAuto, defaultBuyerAddress = defaultBuyerAddress,
)
