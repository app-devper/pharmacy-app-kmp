package app.devper.pharm.ui.i18n.groups

interface ValidationStrings {
    val validationRequired: (String) -> String
    val validationInvalidDate: (String) -> String
    val validationNotANumber: (String) -> String
    val validationMustBePositive: (String) -> String
    val validationMustBeNonNegative: (String) -> String
    val fieldDate: String
    val fieldQuantity: String
    val fieldAmount: String
    val fieldValue: String
    val fieldDrug: String
    val fieldDrugName: String
    val fieldLotNumber: String
    val fieldExpiryDate: String
    val fieldUnit: String
    val fieldPricePerUnit: String
    val fieldBalance: String
    val fieldTotalValue: String
}
