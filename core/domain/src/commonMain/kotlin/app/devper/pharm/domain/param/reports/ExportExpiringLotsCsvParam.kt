package app.devper.pharm.domain.param.reports

import app.devper.pharm.domain.model.ExpiringLot

data class ExportExpiringLotsCsvParam(
    val windowTag: String,
    val rows: List<ExpiringLot>,
    val headers: List<String> = emptyList(),
)
