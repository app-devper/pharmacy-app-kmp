package app.devper.pharm.domain.model

enum class LabelSize(val wire: String, val widthMm: Int, val heightMm: Int) {
    Small("38x25", 38, 25),
    Medium("50x30", 50, 30);

    companion object {
        fun fromWire(raw: String?): LabelSize = entries.firstOrNull { it.wire == raw } ?: Small
    }
}

data class LabelLine(
    val drugId: String,
    val drugName: String,
    val lotNumber: String,
    val barcode: String,
    val price: Double,
    val includePrice: Boolean,
    val copies: Int,
)
