package app.devper.pharm.presentation.settings

import androidx.lifecycle.viewModelScope
import app.devper.pharm.common.error.ErrorMessages
import app.devper.pharm.common.userMessageOr
import app.devper.pharm.domain.model.KySettings
import app.devper.pharm.domain.model.PharmacistInfo
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.model.StoreInfo
import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.param.ReceiptSettingsInput
import app.devper.pharm.domain.param.StockSettingsInput
import app.devper.pharm.domain.param.UpdateSettingsParam
import app.devper.pharm.domain.usecase.RefreshSettingsUseCase
import app.devper.pharm.domain.usecase.UpdateSettingsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private const val LOAD_SETTINGS_FAILED = "โหลดการตั้งค่าไม่สำเร็จ"
private const val SAVED_OK = "บันทึกแล้ว"

class SettingsEditorViewModel(
    settings: SettingsProvider,
    private val refreshSettings: RefreshSettingsUseCase,
    private val updateSettings: UpdateSettingsUseCase,
) : BaseLoadableViewModel<SettingsEditorUiState>(SettingsEditorUiState()) {

    private var hydrated = false

    init {
        settings.state
            .onEach { s ->
                if (!hydrated) {
                    val fields = s.toForm()
                    setState { copy(baseline = fields, form = fields) }
                    hydrated = true
                }
            }
            .launchIn(viewModelScope)

        launchLoad(
            block = { refreshSettings() },
            fallback = LOAD_SETTINGS_FAILED,
            onSuccess = { fresh ->
                hydrated = true
                val fields = fresh.toForm()
                copy(baseline = fields, form = if (dirty) form else fields)
            },
        )
    }

    fun reload() {
        if (current.saving) return
        launchLoad(
            block = { refreshSettings() },
            fallback = LOAD_SETTINGS_FAILED,
            onSuccess = { fresh ->
                val fields = fresh.toForm()
                copy(baseline = fields, form = if (dirty) form else fields)
            },
        )
    }

    fun selectTab(tab: SettingsTab) = setState { copy(tab = tab) }

    fun onStoreName(v: String) = patch { copy(storeName = v) }
    fun onStoreAddress(v: String) = patch { copy(storeAddress = v) }
    fun onStorePhone(v: String) = patch { copy(storePhone = v) }
    fun onStoreTaxId(v: String) = patch { copy(storeTaxId = v) }
    fun onReceiptHeader(v: String) = patch { copy(receiptHeader = v) }
    fun onReceiptFooter(v: String) = patch { copy(receiptFooter = v) }
    fun onReceiptPaperWidth(v: String) = patch { copy(receiptPaperWidth = v) }
    fun onReceiptShowPharmacist(v: Boolean) = patch { copy(receiptShowPharmacist = v) }
    fun onStockLowThreshold(v: String) = patch { copy(stockLowThreshold = v.intOnly()) }
    fun onStockReorderDays(v: String) = patch { copy(stockReorderDays = v.intOnly()) }
    fun onStockReorderLookahead(v: String) = patch { copy(stockReorderLookahead = v.intOnly()) }
    fun onStockExpiringDays(v: String) = patch { copy(stockExpiringDays = v.intOnly()) }
    fun onPharmacistName(v: String) = patch { copy(pharmacistName = v) }
    fun onPharmacistLicenseNo(v: String) = patch { copy(pharmacistLicenseNo = v) }
    fun onKySkipAuto(v: Boolean) = patch { copy(kySkipAuto = v) }
    fun onKyDefaultBuyerAddress(v: String) = patch { copy(kyDefaultBuyerAddress = v) }
    fun onTimezone(v: String) = patch { copy(timezone = v) }

    fun submit() {
        if (!current.canSave) return
        val f = current.form
        setState { copy(saving = true, error = null, message = null) }
        launchResult(
            block = { updateSettings(f.toParam()) },
            onSuccess = { fresh ->
                val fields = fresh.toForm()
                setState { copy(saving = false, baseline = fields, form = fields, message = SAVED_OK) }
            },
            onFailure = { e -> setState { copy(saving = false, error = e.userMessageOr(ErrorMessages.SAVE_FAILED)) } },
        )
    }

    fun dismissMessage() = setState { copy(message = null) }

    private fun patch(transform: SettingsFormFields.() -> SettingsFormFields) {
        setState { copy(form = form.transform()) }
    }

    private fun Settings.toForm(): SettingsFormFields = SettingsFormFields(
        storeName = store.name,
        storeAddress = store.address,
        storePhone = store.phone,
        storeTaxId = store.taxId,

        receiptHeader = receipt.header,
        receiptFooter = receipt.footer,
        receiptPaperWidth = receipt.paperWidth,
        receiptShowPharmacist = receipt.showPharmacist,

        stockLowThreshold = stock.lowStockThreshold.toString(),
        stockReorderDays = stock.reorderDays.toString(),
        stockReorderLookahead = stock.reorderLookahead.toString(),
        stockExpiringDays = stock.expiringDays.toString(),

        pharmacistName = pharmacist.name,
        pharmacistLicenseNo = pharmacist.licenseNo,
        kySkipAuto = ky.skipAuto,
        kyDefaultBuyerAddress = ky.defaultBuyerAddress,
        timezone = timezone,
    )

    private fun SettingsFormFields.toParam(): UpdateSettingsParam = UpdateSettingsParam(
        store = StoreInfo(
            name = storeName.trim(),
            address = storeAddress.trim(),
            phone = storePhone.trim(),
            taxId = storeTaxId.trim(),
        ),
        receipt = ReceiptSettingsInput(
            header = receiptHeader,
            footer = receiptFooter,
            paperWidth = receiptPaperWidth,
            showPharmacist = receiptShowPharmacist,
        ),
        stock = StockSettingsInput(
            lowStockThreshold = stockLowThreshold.toIntOrNull() ?: 0,
            reorderDays = stockReorderDays.toIntOrNull() ?: 30,
            reorderLookahead = stockReorderLookahead.toIntOrNull() ?: 14,
            expiringDays = stockExpiringDays.toIntOrNull() ?: 60,
        ),
        pharmacist = PharmacistInfo(
            name = pharmacistName.trim(),
            licenseNo = pharmacistLicenseNo.trim(),
        ),
        ky = KySettings(
            skipAuto = kySkipAuto,
            defaultBuyerAddress = kyDefaultBuyerAddress,
        ),
        timezone = timezone.trim().ifBlank { "Asia/Bangkok" },
    )
}

private fun String.intOnly(): String = filter { it.isDigit() }
