package app.devper.pharm.presentation.settings

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
)

data class SettingsEditorUiState(
    val baseline: SettingsFormFields = SettingsFormFields(),
    val form: SettingsFormFields = SettingsFormFields(),
    val tab: SettingsTab = SettingsTab.Store,
    override val loading: Boolean = false,
    val saving: Boolean = false,
    val message: String? = null,
    override val error: String? = null,
) : LoadableUiState<SettingsEditorUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withError(value: String?) = copy(error = value)

    val dirty: Boolean get() = form != baseline
    val canSave: Boolean
        get() = !saving && !loading && dirty &&
            form.storeName.isNotBlank() &&
            form.receiptPaperWidth in setOf("58", "80")
}

enum class SettingsTab(val label: String) {
    Store("ร้านค้า"),
    Receipt("ใบเสร็จ"),
    Stock("สต็อก"),
    Pharmacist("เภสัชกร"),
    Ky("ขย."),
}
