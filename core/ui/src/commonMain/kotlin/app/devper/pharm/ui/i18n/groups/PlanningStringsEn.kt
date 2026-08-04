package app.devper.pharm.ui.i18n.groups

object PlanningStringsEn : PlanningStrings {
    override val planningLoadLowStockFailed = "Failed to load low-stock drugs"
    override val planningLoadReorderFailed = "Failed to load reorder suggestions"
    override val planningTitle = "Order recommendations"
    override val planningRefreshCta = "Refresh"
    override val planningAddPoCta = "Add PO"
    override val planningAddAllCta = "Add all"
    override val planningAddRemainingCta: (Int) -> String = { count -> "Add remaining ($count)" }
    override val planningAddedBadge = "Added"
    override val planningAddedMessage: (Int) -> String = { count -> "Added $count item(s) to the purchase order" }
    override val planningDismissCta = "Hide suggestion"
    override val planningOpenPoCta: (Int) -> String = { n -> "Create purchase order ($n)" }
    override val planningLowStockTitle = "Low stock"
    override val planningBelowMinTitle = "Below minimum stock"
    override val planningLowStockSearchPlaceholder = "Search drug, generic, or barcode…"
    override val planningLowStockNotFound = "No low-stock drugs match your search"
    override val planningReorderTitle = "Suggested reorder list"
    override val planningLowStockEmpty = "No drugs running low"
    override val planningBelowMinEmpty = "All drugs above minimum stock"
    override val planningReorderEmpty = "No drugs reach reorder threshold yet"
    override val planningReorderEmptyTitle = "No items to order"
    override val planningHeaderMin = "Minimum"
    override val planningHeaderRecommend = "Recommend"
    override val planningHeaderTotalCost = "Total cost"
    override val planningCountNoun = "items"
    override val planningMetaLine: (String, String) -> String = { rate, daysLeft -> "Avg $rate/day · $daysLeft remaining" }
    override val planningDaysLeftLabel: (Int) -> String = { days -> "$days day(s)" }
    override val planningTrackStockFailed = "Failed to track stock changes"
}
