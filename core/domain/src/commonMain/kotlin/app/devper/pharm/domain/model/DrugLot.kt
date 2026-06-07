package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import kotlinx.datetime.LocalDate

data class DrugLot(
    val id: String,
    val drugId: String,
    val drugName: String? = null,
    val lotNumber: String,
    val expiryDate: LocalDate?,
    val importDate: LocalDate?,
    val costPrice: Money? = null,
    val sellPrice: Money? = null,
    val quantity: Quantity,
    val remaining: Quantity,
)
