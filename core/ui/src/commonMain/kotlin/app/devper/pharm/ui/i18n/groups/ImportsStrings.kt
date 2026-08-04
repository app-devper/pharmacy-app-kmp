package app.devper.pharm.ui.i18n.groups

interface ImportsStrings {
    val importsQtyPieces: (Int) -> String

    val importsListLoadFailed: String
    val importsDetailLoadFailed: String
    val importsFormLoadOrderFailed: String
    val importsFormLoadSuppliersFailed: String
    val importsFormLoadDrugsFailed: String
    val importsConfirmFailed: String
    val importsTitle: String
    val importsSubtitle: String
    val importsSearchPlaceholder: String
    val importsCountNoun: String
    val importsAddCta: String
    val importsListEmpty: String
    val importsListNotFound: String
    val importsHeaderDocNo: String
    val importsHeaderSupplier: String
    val importsHeaderTotal: String
    val importsHeaderCreatedAt: String
    val importsStatusDraft: String
    val importsStatusReceived: String
    val importsStatusReceivedDetail: String
    val importsActionView: String
    val importsActionConfirmReceive: String
    val importsActionAddLine: String
    val importsActionRemoveLine: String
    val importsFormInfoSection: String
    val importsFormDocNo: String
    val importsFormDocNoPlaceholder: String
    val importsFormSupplier: String
    val importsFormSupplierPlaceholder: String
    val importsFormSupplierPickerTitle: String
    val importsFormSupplierSearchPlaceholder: String
    val importsFormReceiveDate: String
    val importsFormCreatedAt: String
    val importsFormConfirmedAt: String
    val importsItemListLabel: String
    val importsFormItemListTitle: (Int) -> String
    val importsFormItemTotalLabel: String
    val importsFormItemTotal: (String) -> String
    val importsFormItemLotLine: (String, String) -> String
    val importsFormPickDrug: String
    val importsFormPickDrugPlaceholder: String
    val importsFormPickDrugTitle: String
    val importsFormPickDrugSearchPlaceholder: String
    val importsFormHeaderLotNumber: String
    val importsFormHeaderLotNumberPlaceholder: String
    val importsFormHeaderExpiry: String
    val importsExpiryDateLabel: String
    val importsFormHeaderCostPrice: String
    val importsFormHeaderSellPrice: String
    val importsFormHeaderOptions: String
    val importsFormReceivedAll: String
    val importsFormEditTitle: String
    val importsNewTitle: String
    val importsFormReceivedBadge: String
    val importsFormReceivedConfirmedHint: String
    val importsConfirmReceiveTitle: String
    val importsConfirmReceiveSubtitle: String
    val importsConfirmReceiveMessage: String
    val importsConfirmReceiveCta: String
    val importsConfirmDeleteDraftTitle: String
    val importsConfirmDeleteDraftMessage: String
    val importsConfirmDeleteReceivedTitle: String
    val importsConfirmDeleteReceivedMessage: String
    val importsHeaderInvoiceNo: String
    val importsHeaderInvoicePlaceholder: String
    val importsDetailTitle: String
}
