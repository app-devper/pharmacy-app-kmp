package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.KySettingsDto
import app.devper.pharm.data.remote.dto.PharmacistInfoDto
import app.devper.pharm.data.remote.dto.ReceiptSettingsDto
import app.devper.pharm.data.remote.dto.SettingsDto
import app.devper.pharm.data.remote.dto.SettingsInputDto
import app.devper.pharm.data.remote.dto.StockSettingsDto
import app.devper.pharm.data.remote.dto.StoreInfoDto
import app.devper.pharm.domain.model.KySettings
import app.devper.pharm.domain.model.PharmacistInfo
import app.devper.pharm.domain.model.ReceiptSettings
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.model.StockSettings
import app.devper.pharm.domain.model.StoreInfo
import app.devper.pharm.domain.param.settings.UpdateSettingsParam

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

internal fun UpdateSettingsParam.toDto(): SettingsInputDto = SettingsInputDto(
    store = StoreInfoDto(
        name = store.name,
        address = store.address,
        phone = store.phone,
        taxId = store.taxId,
    ),
    receipt = ReceiptSettingsDto(
        header = receipt.header,
        footer = receipt.footer,
        paperWidth = receipt.paperWidth,
        showPharmacist = receipt.showPharmacist,
    ),
    stock = StockSettingsDto(
        lowStockThreshold = stock.lowStockThreshold,
        reorderDays = stock.reorderDays,
        reorderLookahead = stock.reorderLookahead,
        expiringDays = stock.expiringDays,
    ),
    pharmacist = PharmacistInfoDto(
        name = pharmacist.name,
        licenseNo = pharmacist.licenseNo,
    ),
    ky = KySettingsDto(
        skipAuto = ky.skipAuto,
        defaultBuyerAddress = ky.defaultBuyerAddress,
    ),
    timezone = timezone,
)
