package app.devper.pharm.domain.param

import kotlinx.datetime.LocalDate

data class AddLotParam(
    val drugId: String,
    val lotNumber: String,
    val expiryDate: LocalDate,
    val importDate: LocalDate? = null,
    val costPrice: Double? = null,
    val sellPrice: Double? = null,
    val quantity: Int,
)

data class DeleteLotParam(
    val drugId: String,
    val lotId: String,
)
