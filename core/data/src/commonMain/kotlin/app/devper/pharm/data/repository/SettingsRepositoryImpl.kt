package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.SettingsApi
import app.devper.pharm.data.remote.dto.KySettingsDto
import app.devper.pharm.data.remote.dto.PharmacistInfoDto
import app.devper.pharm.data.remote.dto.ReceiptSettingsDto
import app.devper.pharm.data.remote.dto.SettingsDto
import app.devper.pharm.data.remote.dto.SettingsInputDto
import app.devper.pharm.data.remote.dto.StockSettingsDto
import app.devper.pharm.data.remote.dto.StoreInfoDto
import app.devper.pharm.domain.model.KySettings
import app.devper.pharm.domain.model.PharmacistInfo
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.model.StoreInfo
import app.devper.pharm.domain.param.UpdateSettingsParam
import app.devper.pharm.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val api: SettingsApi,
) : SettingsRepository {

    private val _settings = MutableStateFlow(Settings())
    override val settings: StateFlow<Settings> = _settings.asStateFlow()

    override suspend fun refresh(): Settings {
        val dto = api.get()
        val mapped = dto.toDomain()
        _settings.value = mapped
        return mapped
    }

    override suspend fun update(param: UpdateSettingsParam): Settings {
        val dto = api.put(param.toDto())
        val mapped = dto.toDomain()
        _settings.value = mapped
        return mapped
    }

    private fun SettingsDto.toDomain() = Settings(
        store = store.toDomain(),
        pharmacist = pharmacist.toDomain(),
        ky = ky.toDomain(),
        timezone = timezone.takeIf { it.isNotBlank() } ?: "Asia/Bangkok",
    )

    private fun StoreInfoDto.toDomain() = StoreInfo(
        name = name, address = address, phone = phone, taxId = taxId,
    )

    private fun PharmacistInfoDto.toDomain() = PharmacistInfo(
        name = name, licenseNo = licenseNo,
    )

    private fun KySettingsDto.toDomain() = KySettings(
        skipAuto = skipAuto, defaultBuyerAddress = defaultBuyerAddress,
    )

    private fun UpdateSettingsParam.toDto() = SettingsInputDto(
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
}
