package app.devper.pharm.ui.i18n.groups

interface MovementsStrings {
    val movementsSpecImport: String
    val movementsSpecSale: String
    val movementsSpecReturn: String
    val movementsSpecAdjustment: String
    val movementsSpecWriteoff: String
    val movementsSpecVoided: String

    val movementsSubtitle: String
    val movementsSearchPlaceholder: String
    val movementsCountNoun: String
    val movementsEmpty: String
    val movementsHeaderType: String
    val movementsHeaderRef: String
    val movementsHeaderBy: String
    val movementsPrevPage: String
    val movementsNextPage: String
    val movementsPagination: (Int, Int) -> String
    val movementsShownOf: (Int, Int) -> String
    val movementsLoadHistoryFailed: String

    val movementsCsvHeaderAt: String
    val movementsCsvHeaderType: String
    val movementsCsvHeaderDrug: String
    val movementsCsvHeaderQty: String
    val movementsCsvHeaderRef: String
    val movementsCsvHeaderNote: String
}
