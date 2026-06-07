package app.devper.pharm.domain.model

import kotlinx.datetime.LocalDate

data class DrugLot(
    val id: String,
    val drugId: String,
    val drugName: String? = null,
    val lotNumber: String,
    val expiryDate: LocalDate?,
    val importDate: LocalDate?,
    val costPrice: Double? = null,
    val sellPrice: Double? = null,
    val quantity: Int,
    val remaining: Int,
)
