package app.devper.pharm.presentation.settings

import app.devper.pharm.common.AppException
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.domain.validation.isValidTimeZoneId
import app.devper.pharm.ui.common.LoadableUiState

data class SettingsFormFields(

    val storeName: String = "",
    val storeAddress: String = "",
    val storePhone: String = "",
    val storeTaxId: String = "",

    val receiptHeader: String = "",
    val receiptFooter: String = "",
    val receiptPaperWidth: String = "58",
    val receiptShowPharmacist: Boolean = false,

    val stockLowThreshold: String = "0",
    val stockReorderDays: String = "30",
    val stockReorderLookahead: String = "14",
    val stockExpiringDays: String = "60",

    val pharmacistName: String = "",
    val pharmacistLicenseNo: String = "",

    val kySkipAuto: Boolean = false,
    val kyDefaultBuyerAddress: String = "",

    val timezone: String = "Asia/Bangkok",
) {
    val storeNameValid: Boolean get() = storeName.isNotBlank()
    val timezoneValid: Boolean get() = timezone.isValidTimeZoneId()
    val receiptPaperWidthValid: Boolean get() = receiptPaperWidth in setOf("58", "80")
    val stockLowThresholdValid: Boolean get() = stockLowThreshold.toIntOrNull()?.let { it >= 0 } == true
    val stockReorderDaysValid: Boolean get() = stockReorderDays.toIntOrNull()?.let { it in 1..365 } == true
    val stockReorderLookaheadValid: Boolean get() = stockReorderLookahead.toIntOrNull()?.let { it in 1..180 } == true
    val stockExpiringDaysValid: Boolean get() = stockExpiringDays.toIntOrNull()?.let { it in 1..365 } == true

    val valid: Boolean
        get() = storeNameValid &&
            timezoneValid &&
            receiptPaperWidthValid &&
            stockLowThresholdValid &&
            stockReorderDaysValid &&
            stockReorderLookaheadValid &&
            stockExpiringDaysValid

    val firstInvalidTab: SettingsTab?
        get() = when {
            !storeNameValid || !timezoneValid -> SettingsTab.Store
            !receiptPaperWidthValid -> SettingsTab.Receipt
            !stockLowThresholdValid ||
                !stockReorderDaysValid ||
                !stockReorderLookaheadValid ||
                !stockExpiringDaysValid -> SettingsTab.Stock
            else -> null
        }
}

data class SettingsEditorUiState(
    val baseline: SettingsFormFields = SettingsFormFields(),
    val form: SettingsFormFields = SettingsFormFields(),
    val tab: SettingsTab = SettingsTab.Store,
    val confirmKySkip: Boolean = false,
    val theme: String = "auto",
    val fontSize: String = "md",
    val density: String = "comfortable",
    val locale: String = "th",
    val localeChangeApplied: Boolean = false,
    override val loading: Boolean = false,
    val saving: Boolean = false,
    val messageState: CommonUiStateMessage? = null,
    val errorState: AppException? = null,
) : LoadableUiState<SettingsEditorUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val dirty: Boolean get() = form != baseline
    val canSave: Boolean
        get() = !saving && !loading && dirty && form.valid
    val tabSaves: Boolean get() = tab.saves
}

enum class SettingsTab {
    Store, Receipt, Stock, Pharmacist, Ky, Display;

    val saves: Boolean get() = this != Display
}
