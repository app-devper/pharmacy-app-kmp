package app.devper.pharm.ui.i18n.groups

interface SettingsStrings {
    val settingsLoadFailed: String
    val settingsToolbarSubtitle: String
    val settingsDirtySubtitle: String
    val settingsTabStore: String
    val settingsTabReceipt: String
    val settingsTabStock: String
    val settingsTabPharmacist: String
    val settingsTabKy: String
    val settingsStoreNameLabel: String
    val settingsStoreNamePlaceholder: String
    val settingsStoreAddress: String
    val settingsStoreTaxId: String
    val settingsStoreTimezone: String
    val settingsStoreTimezoneInvalid: String
    val settingsReceiptHeader: String
    val settingsReceiptHeaderPlaceholder: String
    val settingsReceiptFooter: String
    val settingsReceiptFooterPlaceholder: String
    val settingsReceiptFooterHint: String
    val settingsReceiptPaperWidth: String
    val settingsReceiptPaperWidthInvalid: String
    val settingsReceiptShowPharmacist: String
    val settingsStockLowThresholdLabel: String
    val settingsStockLowThresholdPlaceholder: String
    val settingsStockReorderDays: String
    val settingsStockReorderLookahead: String
    val settingsStockExpiringDays: String
    val settingsStockRangeError: (String, Int, Int) -> String
    val settingsPharmacistName: String
    val settingsPharmacistLicenseNo: String
    val settingsKySkipAuto: String
    val settingsKySkipAutoHint: String
    val settingsKySkipConfirmTitle: String
    val settingsKySkipConfirmBody: String
    val settingsKySkipConfirmCta: String
    val settingsTestPrintCta: String
    val settingsTestPrintSampleItem: String
    val settingsTestPrintFailed: String
    val settingsKyDefaultBuyerAddress: String
    val settingsKyDefaultBuyerAddressHint: String
}
