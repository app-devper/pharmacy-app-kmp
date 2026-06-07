package app.devper.pharm.domain.param

import kotlinx.datetime.LocalDate

data class ReportRangeParam(
    val from: LocalDate? = null,
    val to: LocalDate? = null,
)

data class DashboardRangeParam(
    val days: Int = 7,
)

data class TopOrSlowDrugsParam(
    val days: Int,
)

data class EodReportParam(
    val date: LocalDate? = null,
)

data class CloseEodParam(
    val date: LocalDate? = null,
)
