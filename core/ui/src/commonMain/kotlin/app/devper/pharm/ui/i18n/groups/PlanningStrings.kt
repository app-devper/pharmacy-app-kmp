package app.devper.pharm.ui.i18n.groups

interface PlanningStrings {
    val planningTrackStockFailed: String
    val planningTitle: String
    val planningRefreshCta: String
    val planningAddPoCta: String
    val planningAddAllCta: String
    val planningOpenPoCta: (Int) -> String
    val planningLowStockTitle: String
    val planningBelowMinTitle: String
    val planningReorderTitle: String
    val planningLowStockEmpty: String
    val planningBelowMinEmpty: String
    val planningReorderEmpty: String
    val planningReorderEmptyTitle: String
    val planningHeaderMin: String
    val planningHeaderRecommend: String
    val planningHeaderTotalCost: String
    val planningCountNoun: String
    val planningMetaLine: (String, String) -> String
    val planningDaysLeftLabel: (Int) -> String
}
