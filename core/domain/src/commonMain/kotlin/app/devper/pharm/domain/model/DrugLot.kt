package app.devper.pharm.domain.model

data class DrugLot(
    val id: String,
    val drugId: String,
    val drugName: String? = null,
    val lotNumber: String,
    val expiryDate: String,
    val importDate: String,
    val costPrice: Double? = null,
    val sellPrice: Double? = null,
    val quantity: Int,
    val remaining: Int,
)
