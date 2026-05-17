package app.devper.pharm.domain.model

data class AltUnit(
    val name: String,
    val factor: Int,
    val sellPrice: Double,
    val prices: Map<String, Double> = emptyMap(),
    val barcode: String? = null,
    val hidden: Boolean = false,
)
