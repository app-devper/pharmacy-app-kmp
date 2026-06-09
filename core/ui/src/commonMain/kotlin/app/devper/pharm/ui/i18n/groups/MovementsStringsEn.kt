package app.devper.pharm.ui.i18n.groups

object MovementsStringsEn : MovementsStrings {
    override val movementsSubtitle = "Stock in/out history"
    override val movementsSearchPlaceholder = "Search drug name…"
    override val movementsCountNoun = "items"
    override val movementsEmpty = "No items in this period"
    override val movementsHeaderType = "Type"
    override val movementsHeaderRef = "Reference"
    override val movementsHeaderBy = "By"
    override val movementsPrevPage = "‹ Prev"
    override val movementsNextPage = "Next ›"
    override val movementsPagination: (Int, Int) -> String = { page, total -> "Page $page / $total" }
    override val movementsShownOf: (Int, Int) -> String = { shown, total -> "Showing $shown of $total items" }
    override val movementsLoadHistoryFailed = "Failed to load history"
}
