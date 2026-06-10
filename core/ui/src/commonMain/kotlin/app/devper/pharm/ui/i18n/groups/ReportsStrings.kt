package app.devper.pharm.ui.i18n.groups

interface ReportsStrings {
    val reportsAvgMargin: String

    val reportsLoadSummaryFailed: String
    val reportsLoadReportFailed: String
    val reportsEodCloseFailed: String
    val reportsEodPrintUnsupported: String
    val reportsSubtitle: String
    val reportsTabSummary: String
    val reportsTabProfit: String
    val reportsTabEod: String
    val reportsMetricSalesToday: String
    val reportsMetricSalesMonth: String
    val reportsMetricProfitMonthApprox: String
    val reportsMetricProfitMonthHint: String
    val reportsMetricStockValue: String
    val reportsMetricStockHint: (Int, Int) -> String
    val reportsRangeToday: String
    val reportsRangeThisWeek: String
    val reportsRangeThisMonth: String
    val reportsRangeLastMonth: String
    val reportsEmptyDay: String
    val reportsEmptyNoBills: String
    val reportsEmptyNoData: String
    val reportsEmptyChartHint: String
    val reportsSectionDailySales: String
    val reportsSectionDailySalesEmpty: String
    val reportsSectionTopBills: String
    val reportsSectionMonthly: String
    val reportsAvgPerDay: (String) -> String
    val reportsHeaderQtySold: String
    val reportsHeaderBills: String
    val reportsHeaderRevenue: String
    val reportsHeaderCost: String
    val reportsHeaderProfit: String
    val reportsHeaderDrugName: String
    val reportsTotalLabel: String
    val reportsRevenueVsCostLabel: String
    val reportsProfitTitle: String
    val reportsProfitSubtitle: String
    val reportsProfitTotal: String
    val reportsProfitBeforeCost: String
    val reportsProfitRevenue: String
    val reportsProfitCost: String
    val reportsTopSellingTitle: String
    val reportsProfitTopSelling: String
    val reportsProfitHighMargin: String
    val reportsProfitNoMovement: String
    val reportsProfitSetCostHint: String
    val reportsProfitMissingCostBanner: (Int) -> String
    val reportsProfitLossExample: String
    val reportsRevenueMinusCost: String
    val reportsCostBasis: String
    val reportsDrugWord: String
    val reportsSortBy: String
    val reportsDatePlaceholder: String
    val reportsEodTitle: String
    val reportsEodSubtitle: String
    val reportsEodDate: String
    val reportsEodToday: String
    val reportsEodConfirmTitle: String
    val reportsEodConfirmMessage: String
    val reportsEodCloseCta: String
    val reportsEodClosedBadge: String
    val reportsEodTryDifferentRange: String
    val reportsEodTryAnotherDate: String
    val reportsEodPrintCta: String
    val reportsEodChannelSum: String
    val reportsEodDayTotal: String
    val reportsEodNetSalesLabel: String
    val reportsEodTotalDiscount: String
    val reportsEodCashIn: String
    val reportsEodCashReceived: String
    val reportsEodChangeOut: String
    val reportsEodReceiveMinusChange: String
    val reportsEodDrawerMatches: String
    val reportsEodDrawerMismatches: String
    val reportsEodNetSalesLine: (String, Int) -> String
    val reportsEodNetSalesAndCashLine: (String, String) -> String
    val reportsEodCashLine: (String) -> String
    val reportsEodClosedDate: (String) -> String
    val reportsBillsOfDay: (Int) -> String
    val reportsWalkInCustomer: String
}
