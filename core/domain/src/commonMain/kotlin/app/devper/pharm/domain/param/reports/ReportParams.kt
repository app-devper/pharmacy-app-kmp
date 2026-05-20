package app.devper.pharm.domain.param

data class ReportRangeParam(
    val from: String = "",
    val to: String = "",
)

data class DashboardRangeParam(
    val days: Int = 7,
)

data class TopOrSlowDrugsParam(
    val days: Int,
)

data class EodReportParam(
    val date: String = "",
)

data class CloseEodParam(
    val date: String = "",
)
