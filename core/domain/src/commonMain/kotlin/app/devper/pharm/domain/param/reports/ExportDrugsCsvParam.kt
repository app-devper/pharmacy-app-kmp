package app.devper.pharm.domain.param.reports

import app.devper.pharm.domain.model.Drug

data class ExportDrugsCsvParam(
    val rows: List<Drug>,
    val headers: List<String> = emptyList(),
)
