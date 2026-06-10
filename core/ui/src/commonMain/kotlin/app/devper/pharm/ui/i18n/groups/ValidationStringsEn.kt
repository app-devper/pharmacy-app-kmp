package app.devper.pharm.ui.i18n.groups

object ValidationStringsEn : ValidationStrings {
    override val validationRequired: (String) -> String = { label -> "$label is required" }
    override val validationInvalidDate: (String) -> String = { label -> "$label is invalid (format YYYY-MM-DD)" }
    override val validationNotANumber: (String) -> String = { label -> "$label must be a number" }
    override val validationMustBePositive: (String) -> String = { label -> "$label must be greater than 0" }
    override val validationMustBeNonNegative: (String) -> String = { label -> "$label must not be negative" }
    override val fieldDate = "Date"
    override val fieldQuantity = "Quantity"
    override val fieldAmount = "Amount"
    override val fieldValue = "Value"
    override val fieldDrug = "Drug"
    override val fieldDrugName = "Drug name"
    override val fieldLotNumber = "Lot number"
    override val fieldExpiryDate = "Expiry date"
    override val fieldUnit = "Unit"
    override val fieldPricePerUnit = "Price per unit"
    override val fieldBalance = "Balance"
    override val fieldTotalValue = "Total value"
}
