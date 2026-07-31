package app.devper.pharm.ui.i18n.groups

object ExpiryStringsEn : ExpiryStrings {
    override val expiryLoadLotsFailed = "Failed to load expiry lots"
    override val expiryWriteoffFailures: (Int) -> String = { n -> "$n lot(s) failed — please retry" }
    override val expiryMoreFailures: (Int) -> String = { n -> "(+$n more)" }

    override val expiryWindow30 = "30 days"
    override val expiryWindow60 = "60 days"
    override val expiryWindow90 = "90 days"
    override val expiryWindow180 = "180 days"
    override val expiryWindowExpired = "Expired"

    override val expirySubtitle = "Check near-expiry lots and write off"
    override val expirySearchPlaceholder = "Search drug or lot number…"
    override val expirySearchNotFound = "No lots match your search"
    override val expirySelectAll = "Select all"
    override val expirySelectPartial = "Partial selection · tap to clear"
    override val expiryWriteoffCta = "Write off"
    override val expiryWriteoffSelectedLabel: (Int) -> String = { count -> "Write off $count lot(s)" }
    override val expiryCountNoun = "lots"
    override val expiryTotalRemaining = "Total remaining"
    override val expiryHeaderDrugName = "Drug name"
    override val expiryHeaderLotNumber = "Lot number"
    override val expiryHeaderExpiry = "Expiry date"
    override val expiryHeaderRemaining = "Remaining"
    override val expiryStatusExpired = "Expired"
    override val expiryStatusDaysLeft: (Int) -> String = { days -> "$days day(s) left" }
    override val expiryEmpty = "No lots in this window"
    override val expiryConfirmTitle = "Write off lots?"
    override val expiryConfirmMessage: (Int) -> String = { count ->
        "The system will remove $count lots and reduce stock by each lot's remaining — " +
        "the write-off is logged for audit."
    }
    override val expiryResultSuccessTitle = "Write-off complete"
    override val expiryResultPartialTitle = "Partially written off"
    override val expiryResultSummary: (Int, Int) -> String = { writtenOff, total -> "Recorded $writtenOff/$total lots" }
    override val expiryWriteoffFailed = "Write-off failed"
}
