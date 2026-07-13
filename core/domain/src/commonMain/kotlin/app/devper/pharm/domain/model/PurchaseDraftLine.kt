package app.devper.pharm.domain.model

data class PurchaseDraftLine(
    val drugId: String,
    val drugName: String,
    val qty: Int,
    val costPrice: Double,
    val sellPrice: Double,
)
