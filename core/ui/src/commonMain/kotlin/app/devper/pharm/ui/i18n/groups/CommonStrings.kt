package app.devper.pharm.ui.i18n.groups

interface CommonStrings {
    val commonAppBrand: String
    val commonAppTagline: String
    val commonExpandMenu: String
    val commonCollapseMenu: String
    val commonOffline: String
    val commonErrorIconDesc: String
    val commonOpenMenu: String
    val commonHelp: String
    val commonResultFound: (Int, String, Int) -> String
    val commonResultTotal: (Int, String) -> String
    val commonPendingSyncBadge: (Int) -> String
    val commonItemsCount: (Int) -> String
    val commonNoBillNo: String
    val commonUnnamed: String
    val commonNoDrugName: String
    val commonNoSupplier: String

    val commonUnitDefault: String

    val commonStatusPending: String
    val commonStatusDone: String
    val commonStatusVoided: String
    val commonStatusActive: String
    val commonStatusInactive: String
    val commonStatusDraft: String
    val commonStatusConfirmed: String
    val commonStatusFailed: String
    val commonStatusLowStock: String
    val commonStatusOutOfStock: String
    val commonStatusNormal: String
    val commonStatusBackordered: String
    val commonStatusVip: String
    val commonStatusReturned: String
    val commonDrugTypeHerb: String
    val commonDrugTypeSupplement: String
    val commonDrugTypeRx: String
    val commonOversoldBadge: (Int) -> String
    val commonPresellBadge: String
    val commonStockRemaining: (Int, String) -> String
    val commonOnline: String
    val commonMenu: String
    val commonLogout: String
    val commonSwitchToLightTheme: String
    val commonSwitchToDarkTheme: String
    val commonFrom: String
    val commonTo: String
    val commonRevenue: String
    val commonCost: String

    val commonCancel: String
    val commonSave: String
    val commonDelete: String
    val commonEdit: String
    val commonAdd: String
    val commonSearch: String
    val commonConfirm: String
    val commonClose: String
    val commonBackspace: String
    val commonBack: String
    val commonLoading: String
    val commonRetry: String
    val commonMore: String
    val commonTotal: String
    val commonQty: String
    val commonUnit: String
    val commonPrice: String
    val commonStatus: String
    val commonNote: String
    val commonName: String
    val commonPhone: String
    val commonDate: String
    val commonFilter: String
    val commonExport: String
    val commonPrint: String
    val commonRefresh: String
    val commonUnitPiece: String
    val commonUnitTablet: String
    val commonUnitCapsule: String
    val commonRequired: String
    val commonOptional: String
    val commonYes: String
    val commonNo: String
    val commonPick: String
    val commonBaht: String
    val commonLoadFailed: String
    val commonSaveFailed: String
    val commonDeleteFailed: String
    val commonExportFailed: String
    val commonExportEmpty: String
    val commonSaved: String
    val commonLogoutFailed: String
    val commonThemeChangeFailed: String
    val commonErrorAuth: String
    val commonErrorForbidden: String
    val commonErrorNotFound: String
    val commonErrorConflict: String
    val commonErrorNetwork: String
    val commonErrorServer: String
    val commonErrorValidation: String
    val commonErrorStorage: String
    val commonErrorUnsupported: String
    val commonErrorGeneric: String
}
