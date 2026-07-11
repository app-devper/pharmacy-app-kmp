package app.devper.pharm.ui.i18n.groups

interface SalesHistoryStrings {
    val salesHistoryReturnTitle: (String) -> String
    val salesHistoryReturnSubtitle: String
    val salesHistoryReasonLabel: String
    val salesHistoryReturnReasonPlaceholder: String
    val salesHistoryReturnConfirmCta: String
    val salesHistorySoldRemaining: (Int, String, Int) -> String
    val salesHistoryReturnCapHint: (Int, Int) -> String
    val salesHistoryRefund: (String) -> String
    val salesHistoryBillTitle: (String) -> String
    val salesHistoryVoidedBadge: String
    val salesHistoryReturnedQty: (Int) -> String
    val salesHistoryTotalRow: String
    val salesHistoryDiscountRow: String
    val salesHistoryNetRow: String

    val salesHistoryReturnReasonRequired: String
    val salesHistoryReturnItemsRequired: String

    val salesHistorySubtitle: String
    val salesHistorySearchPlaceholder: String
    val salesHistoryCountNoun: String
    val salesHistoryEmptySearching: String
    val salesHistoryEmptyDateRange: String
    val salesHistoryHeaderTime: String
    val salesHistoryHeaderBillNo: String
    val salesHistoryHeaderNet: String
    val salesHistoryStatsTotal: String
    val salesHistoryWalkInCustomer: String
    val salesHistoryStatusOk: String
    val salesHistoryActionViewBill: String
    val salesHistoryActionReturn: String
    val salesHistoryLoadBillsFailed: String
    val salesHistoryLoadItemsFailed: String
    val salesHistorySubmitReturnFailed: String
    val salesHistoryRangeToday: String
    val salesHistoryRange7d: String
    val salesHistoryRangeMonth: String
}
