package app.devper.pharm.ui.i18n.groups

interface SellStrings {
    val sellParkedSummary: (Int, String) -> String
    val sellDiscountPercentSeg: String

    val sellRemoveLineTitle: String
    val sellLineDetailsDesc: String
    val sellRemoveLineDesc: String
    val sellTierWholesale: String
    val sellTierRegular: String
    val sellTierRetail: String
    val sellQtyDecrease: String
    val sellQtyIncrease: String
    val sellShortcutSubtitle: String
    val sellShortcutAll: String
    val sellShortcutSearch: String
    val sellShortcutCustomer: String
    val sellShortcutCartDiscount: String
    val sellShortcutPark: String
    val sellShortcutParked: String
    val sellShortcutPay: String
    val sellShortcutClose: String
    val sellParkedHintCanPark: String
    val sellParkedHintEmpty: String
    val sellParkSlotHere: String
    val sellParkedDeleteDesc: String
    val sellParkedDeleteTitle: (Int) -> String
    val sellParkedDeleteBody: (Int) -> String
    val sellParkedOverwriteTitle: (Int) -> String
    val sellParkedOverwriteBody: String
    val sellClearCartCta: String
    val sellClearCartBody: (Int) -> String
    val sellIssueReceipt: String
    val sellAllergyTitle: String
    val sellControlledKy: (String) -> String
    val sellKyCaptureHint: String
    val sellPickerCountAll: (Int) -> String
    val sellPickerCountFound: (Int, Int) -> String
    val sellPickerEmptyStock: String
    val sellOversellCount: (Int) -> String
    val sellOversellExplain: String
    val sellOversellReconcileNote: String
    val sellOversellConfirmCheck: String
    val sellOversellNeedHave: (Int, Int) -> String
    val sellOversellShortBadge: (Int) -> String
    val sellKySheetIntro: String
    val sellKySkipCta: String
    val sellKy11Label: String
    val sellKy10Label: String
    val sellKy12Label: String
    val sellDiscountFlatSeg: String
    val sellDiscountPercentField: String
    val sellDiscountFlatField: String
    val sellDiscountPercentInvalid: String
    val sellDiscountFlatInvalid: (String) -> String
    val sellDiscountDeducted: String
    val sellAddDiscount: String
    val sellCartDiscountShort: String
    val sellCartDiscountPercentLabel: (Int) -> String
    val sellVoidBillTitle: (String) -> String
    val sellVoidBillSubtitle: String
    val sellParkedWaiting: (Int) -> String
    val sellViewCart: String
    val sellOpenParked: String
    val sellExactAmount: String
    val sellShortBy: (String) -> String
    val sellReceiptNo: (String) -> String
    val sellLineDiscountField: String
    val sellLineDiscountInvalid: (String) -> String
    val sellParkSlotDesc: (Int) -> String
    val sellParkedToast: (Int) -> String
    val sellOpenViewCta: String
    val sellAddedToast: (String) -> String

    val sellCheckoutEmptyCart: String

    val sellVoidMissingBillId: String
    val sellVoidReasonRequired: String
    val sellVoidFailed: String
    val sellBarcodeNotFound: (String) -> String
    val sellLoadCustomersFailed: String
    val sellPrintReceiptUnsupported: String
    val sellKyIncomplete: (String, String) -> String
    val sellKyError: (String, String) -> String
    val sellOfflineSaved: String
    val sellCheckoutFailed: String

    val sellCart: String
    val sellSearchPlaceholder: String
    val sellNoResults: String
    val sellEmptyCart: String
    val sellEmptyCartHint: String
    val sellSubtotal: String
    val sellTotal: String
    val sellNetTotal: String
    val sellDiscount: String
    val sellDiscountLine: String
    val sellDiscountCart: String
    val sellDiscountPerUnit: String
    val sellDiscountClear: String
    val sellShowSubtotal: String
    val sellHideSubtotal: String
    val sellPayment: String
    val sellReceived: String
    val sellChange: String
    val sellCheckout: String
    val sellCheckoutSave: String
    val sellReceiptDone: String
    val sellReceiptFailed: String
    val sellCustomer: String
    val sellCustomerWalkIn: String
    val sellCustomerSelect: String
    val sellCustomerClear: String
    val sellCustomerSearchPlaceholder: String
    val sellCustomerEmpty: String
    val sellCustomerNotFound: String
    val sellPharmacist: String
    val sellPrescriber: String
    val sellBuyerName: String
    val sellBuyerAddress: String
    val sellPrescriptionNo: String
    val sellHospital: String
    val sellPatient: String
    val sellPurpose: String
    val sellAllergies: String
    val sellSaveKyBeforeBill: String
    val sellControlledRx: (Int) -> String
    val sellControlledNote: String
    val sellSkipKyTitle: String
    val sellSkipKyConfirmCta: String
    val sellSkipKyConfirm: String
    val sellPark: String
    val sellParked: String
    val sellNewBill: String
    val sellCancelBillTitle: String
    val sellCancelBillConfirm: (String) -> String
    val sellCancelBillReason: String
    val sellCancelBillReasonExample: String
    val sellCancelBillCta: String
    val sellShortcuts: String
    val sellPriceOriginal: String
    val sellPriceAfterDiscount: String
    val sellPickUnit: String
    val sellRemaining: String
    val sellShortfall: String
    val sellShortfallShort: (String) -> String
    val sellOversold: String
    val sellOversoldHint: String
    val sellRemoveItemTitle: String
    val sellRemoveCart: String
    val sellPrintCta: String
    val sellScannerOn: String
    val sellOfflineQueueHint: String
}
