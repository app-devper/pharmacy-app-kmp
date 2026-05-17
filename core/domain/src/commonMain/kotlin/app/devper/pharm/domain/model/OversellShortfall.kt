package app.devper.pharm.domain.model

data class OversellShortfall(
    val drugId: String,
    val drugName: String,
    val asked: Int,
    val available: Int,
) {
    val shortfall: Int get() = (asked - available).coerceAtLeast(0)
}
