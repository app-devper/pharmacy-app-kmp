package app.devper.pharm.domain.param.reports

import app.devper.pharm.domain.model.DrugProfit
import kotlinx.datetime.LocalDate

data class ExportProfitCsvParam(
    val from: LocalDate?,
    val to: LocalDate?,
    val rows: List<DrugProfit>,
)
