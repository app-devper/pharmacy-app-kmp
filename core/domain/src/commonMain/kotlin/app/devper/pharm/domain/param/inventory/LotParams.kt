package app.devper.pharm.domain.param.inventory

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import kotlinx.datetime.LocalDate

data class AddLotParam(
    val drugId: String,
    val lotNumber: String,
    val expiryDate: LocalDate,
    val importDate: LocalDate? = null,
    val costPrice: Money? = null,
    val sellPrice: Money? = null,
    val quantity: Quantity,
)

data class DeleteLotParam(
    val drugId: String,
    val lotId: String,
)
