package app.devper.pharm.domain.param

import app.devper.pharm.domain.model.DrugProfit

data class ExportProfitCsvParam(
    val from: String,
    val to: String,
    val rows: List<DrugProfit>,
)
