package app.devper.pharm.domain.param

import app.devper.pharm.domain.model.StockMovement

data class ExportMovementsCsvParam(
    val from: String,
    val to: String,
    val drugName: String,
    val rows: List<StockMovement>,
)
