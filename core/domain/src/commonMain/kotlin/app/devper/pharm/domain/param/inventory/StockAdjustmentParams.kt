package app.devper.pharm.domain.param.inventory

import app.devper.pharm.domain.model.AdjustmentReason

data class AddStockAdjustmentParam(
    val drugId: String,
    val delta: Int,
    val reason: AdjustmentReason,
    val note: String = "",
)
