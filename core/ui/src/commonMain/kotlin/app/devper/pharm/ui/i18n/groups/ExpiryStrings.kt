package app.devper.pharm.ui.i18n.groups

interface ExpiryStrings {
    val expiryWriteoffFailures: (Int) -> String
    val expiryMoreFailures: (Int) -> String

    val expiryWindow30: String
    val expiryWindow60: String
    val expiryWindow90: String
    val expiryWindow180: String
    val expiryWindowExpired: String

    val expirySubtitle: String
    val expirySelectAll: String
    val expirySelectPartial: String
    val expiryWriteoffCta: String
    val expiryWriteoffSelectedLabel: (Int) -> String
    val expiryCountNoun: String
    val expiryTotalRemaining: String
    val expiryHeaderDrugName: String
    val expiryHeaderLotNumber: String
    val expiryHeaderExpiry: String
    val expiryHeaderRemaining: String
    val expiryStatusExpired: String
    val expiryStatusDaysLeft: (Int) -> String
    val expiryEmpty: String
    val expiryConfirmTitle: String
    val expiryConfirmMessage: (Int) -> String
    val expiryResultSuccessTitle: String
    val expiryResultPartialTitle: String
    val expiryResultSummary: (Int, Int) -> String
    val expiryWriteoffFailed: String
}
