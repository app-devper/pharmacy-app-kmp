package app.devper.pharm.domain.param.reports

import app.devper.pharm.domain.model.StockMovement
import kotlinx.datetime.LocalDate

data class ExportMovementsCsvParam(
    val from: LocalDate?,
    val to: LocalDate?,
    val drugName: String,
    val rows: List<StockMovement>,
    val headers: List<String> = emptyList(),
)
