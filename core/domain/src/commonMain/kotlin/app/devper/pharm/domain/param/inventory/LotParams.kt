package app.devper.pharm.domain.param

data class AddLotParam(
    val drugId: String,
    val lotNumber: String,
    val expiryDate: String,
    val importDate: String? = null,
    val costPrice: Double? = null,
    val sellPrice: Double? = null,
    val quantity: Int,
)

data class DeleteLotParam(
    val drugId: String,
    val lotId: String,
)
