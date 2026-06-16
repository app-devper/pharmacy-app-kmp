package app.devper.pharm.ui.i18n.groups

object SalesHistoryStringsEn : SalesHistoryStrings {
    override val salesHistoryReturnTitle: (String) -> String = { bill -> "Return items from bill $bill" }
    override val salesHistoryReturnSubtitle = "Pick quantities to return — the refund is calculated automatically"
    override val salesHistoryReasonLabel = "Reason"
    override val salesHistoryReturnReasonPlaceholder = "Return reason, e.g. customer changed mind, defective"
    override val salesHistoryReturnConfirmCta = "Confirm return"
    override val salesHistorySoldRemaining: (Int, String, Int) -> String = { q, u, r -> "Sold $q $u · returnable $r" }
    override val salesHistoryRefund: (String) -> String = { v -> "Refund $v" }
    override val salesHistoryBillTitle: (String) -> String = { bill -> "Bill $bill" }
    override val salesHistoryVoidedBadge = "Voided"
    override val salesHistoryReturnedQty: (Int) -> String = { n -> "Returned $n" }
    override val salesHistoryTotalRow = "Subtotal"
    override val salesHistoryDiscountRow = "Discount"
    override val salesHistoryNetRow = "Net"

    override val salesHistorySubtitle = "Past sales, returns, and voids"
    override val salesHistorySearchPlaceholder = "Bill number or customer name…"
    override val salesHistoryCountNoun = "bills"
    override val salesHistoryEmptySearching = "No bills match the search"
    override val salesHistoryEmptyDateRange = "No bills in the selected period"
    override val salesHistoryHeaderTime = "Time"
    override val salesHistoryHeaderBillNo = "Bill no."
    override val salesHistoryHeaderNet = "Net total"
    override val salesHistoryStatsTotal = "Gross total"
    override val salesHistoryWalkInCustomer = "Walk-in"
    override val salesHistoryStatusOk = "Completed"
    override val salesHistoryActionViewBill = "View bill"
    override val salesHistoryActionReturn = "Return"
    override val salesHistoryLoadBillsFailed = "Failed to load bills"
    override val salesHistoryLoadItemsFailed = "Failed to load sale items"
    override val salesHistorySubmitReturnFailed = "Failed to record the return"
    override val salesHistoryMetricNetSales = "Net sales"
    override val salesHistoryMetricBills = "Bills"
    override val salesHistoryMetricAvg = "Avg / bill"
    override val salesHistoryMetricVoided = "Voided"
    override val salesHistoryRangeToday = "Today"
    override val salesHistoryRange7d = "7 days"
    override val salesHistoryRangeMonth = "This month"
    override val salesHistoryReturnReasonRequired = "Please provide a return reason"
    override val salesHistoryReturnItemsRequired = "Select at least 1 item to return"
}
