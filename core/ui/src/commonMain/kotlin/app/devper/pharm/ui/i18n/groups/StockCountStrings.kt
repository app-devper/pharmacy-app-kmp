package app.devper.pharm.ui.i18n.groups

interface StockCountStrings {
    val stockCountHistoryTitle: String
    val stockCountHistorySubtitle: String
    val stockCountHistorySearchPlaceholder: String
    val stockCountHistoryNewCta: String
    val stockCountHistoryEmpty: String
    val stockCountHistoryNotFound: String
    val stockCountHistoryCountNoun: String
    val stockCountHeaderRound: String
    val stockCountHeaderItems: String
    val stockCountHeaderDelta: String
    val stockCountHeaderAdjust: String
    val stockCountStatusAdjusted: String
    val stockCountStatusNotAdjusted: String
    val stockCountHeaderNote: String
    val stockCountActionDetails: String
    val stockCountFormSearchPlaceholder: String
    val stockCountFormNotePlaceholder: String
    val stockCountFormCounted: String
    val stockCountFormInSystem: String
    val stockCountFormDelta: String
    val stockCountFormUnitLabel: (String) -> String
    val stockCountFormChangedItems: String
    val stockCountFormFillSystem: String
    val stockCountFormFillConfirmTitle: String
    val stockCountFormFillConfirmMessage: String
    val stockCountFormFillConfirmCta: String
    val stockCountFormSummaryAll: String
    val stockCountFormSummaryAdjusted: String
    val stockCountFormSummaryNotAdjusted: String
    val stockCountFormPrintedShort: String
    val stockCountFormCounted2: String
    val stockCountFormDiscrepancyTotal: String
    val stockCountFormTopDiscrepancy: (Int) -> String
    val stockCountFormClearDraftCta: String
    val stockCountFormClearConfirmTitle: String
    val stockCountFormClearConfirmMessage: String
    val stockCountFormClearConfirmCta: String
    val stockCountFormSaveRoundCta: String
    val stockCountFormResultLine: (Int, Int, Int) -> String
    val stockCountFormStatusLine: (Int, Int, Int, Int) -> String
    val stockCountFormSaveCountLabel: (Int) -> String
    val stockCountFormSummaryDelta: (Int, Int) -> String
    val stockCountFormEmptySearching: String
    val stockCountFormEmptyDefault: String
    val stockCountFormInvalidCount: String
    val stockCountFormConfirmTitle: String
    val stockCountFormConfirmMessage: String
    val stockCountFormConfirmCta: String
}
