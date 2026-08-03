package app.devper.pharm.presentation.settings

import androidx.lifecycle.viewModelScope
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.presentation.settings.exception.SettingsUiStateError
import app.devper.pharm.domain.model.DensityPreference
import app.devper.pharm.domain.model.FontSizePreference
import app.devper.pharm.domain.model.KySettings
import app.devper.pharm.domain.model.LocalePreference
import app.devper.pharm.domain.model.PharmacistInfo
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.model.StoreInfo
import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.observer.UiPreferencesProvider
import app.devper.pharm.domain.param.settings.ReceiptSettingsInput
import app.devper.pharm.domain.param.settings.StockSettingsInput
import app.devper.pharm.domain.param.settings.UpdateSettingsParam
import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.domain.usecase.reports.PrintReceiptUseCase
import app.devper.pharm.domain.usecase.settings.RefreshSettingsUseCase
import app.devper.pharm.domain.usecase.settings.SetDensityPreferenceUseCase
import app.devper.pharm.domain.usecase.settings.SetFontSizePreferenceUseCase
import app.devper.pharm.domain.usecase.settings.SetLocalePreferenceUseCase
import app.devper.pharm.domain.usecase.settings.SetThemePreferenceUseCase
import app.devper.pharm.domain.usecase.settings.UpdateSettingsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class SettingsEditorViewModel(
    settings: SettingsProvider,
    private val refreshSettings: RefreshSettingsUseCase,
    private val updateSettings: UpdateSettingsUseCase,
    private val printReceipt: PrintReceiptUseCase,
    uiPreferences: UiPreferencesProvider,
    private val setTheme: SetThemePreferenceUseCase,
    private val setFontSize: SetFontSizePreferenceUseCase,
    private val setDensity: SetDensityPreferenceUseCase,
    private val setLocale: SetLocalePreferenceUseCase,
) : BaseLoadableViewModel<SettingsEditorUiState>(SettingsEditorUiState()) {

    private var hydrated = false

    init {
        uiPreferences.state
            .onEach { prefs ->
                setState {
                    copy(
                        theme = prefs.theme.wire,
                        fontSize = prefs.fontSize.wire,
                        density = prefs.density.wire,
                        locale = prefs.locale.wire,
                    )
                }
            }
            .launchIn(viewModelScope)

        settings.state
            .onEach { s ->
                if (!hydrated) {
                    val fields = s.toForm()
                    setState { copy(baseline = fields, form = fields) }
                    hydrated = true
                }
            }
            .launchIn(viewModelScope)

        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { refreshSettings() },
            onSuccess = { fresh ->
                hydrated = true
                val fields = fresh.toForm()
                setState { copy(loading = false, baseline = fields, form = if (dirty) form else fields) }
            },
            onFailure = { e -> setState { copy(loading = false, errorState = SettingsUiStateError.LoadSettingsFailed(e)) } },
        )
    }

    fun reload() {
        if (current.saving) return
        launchResult(
            block = { refreshSettings() },
            onSuccess = { fresh ->
                val fields = fresh.toForm()
                setState { copy(baseline = fields, form = if (dirty) form else fields) }
            },
            onFailure = { e -> setState { copy(errorState = SettingsUiStateError.LoadSettingsFailed(e)) } },
        )
    }

    fun selectTab(tab: SettingsTab) = setState { copy(tab = tab) }

    fun onThemeChange(value: String) = setTheme(ThemePreference.parse(value))

    fun onFontSizeChange(value: String) = setFontSize(FontSizePreference.parse(value))

    fun onDensityChange(value: String) = setDensity(DensityPreference.parse(value))

    fun onLocaleChange(value: String) {
        val parsed = LocalePreference.parse(value)
        if (parsed.wire == current.locale) return
        setLocale(parsed)
        setState { copy(localeChangeApplied = true) }
    }

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
    fun onKySkipAuto(v: Boolean) {
        if (v && !current.form.kySkipAuto) setState { copy(confirmKySkip = true) }
        else patch { copy(kySkipAuto = v) }
    }

    fun testPrint(template: ReceiptTemplate) {
        launchResult(
            block = { printReceipt(template) },
            onSuccess = { printed ->
                if (!printed) setState { copy(errorState = SettingsUiStateError.TestPrintFailed()) }
            },
            onFailure = { e -> setState { copy(errorState = SettingsUiStateError.TestPrintFailed(e)) } },
        )
    }

    fun confirmKySkipAuto() {
        setState { copy(confirmKySkip = false) }
        patch { copy(kySkipAuto = true) }
    }

    fun cancelKySkipAuto() = setState { copy(confirmKySkip = false) }
    fun onKyDefaultBuyerAddress(v: String) = patch { copy(kyDefaultBuyerAddress = v) }
    fun onTimezone(v: String) = patch { copy(timezone = v) }

    fun submit() {
        if (!current.canSave) return
        val f = current.form
        setState { copy(saving = true, errorState = null, messageState = null) }
        launchResult(
            block = { updateSettings(f.toParam()) },
            onSuccess = { fresh ->
                val fields = fresh.toForm()
                setState { copy(saving = false, baseline = fields, form = fields, messageState = CommonUiStateMessage.Saved) }
            },
            onFailure = { e -> setState { copy(saving = false, errorState = CommonUiStateError.SaveFailed(e)) } },
        )
    }

    fun dismissMessage() = setState { copy(messageState = null) }

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
