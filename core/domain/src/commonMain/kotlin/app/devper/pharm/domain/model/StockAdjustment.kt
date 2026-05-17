package app.devper.pharm.domain.model

data class StockAdjustment(
    val id: String,
    val drugId: String,
    val drugName: String,
    val delta: Int,
    val before: Int,
    val after: Int,
    val reason: AdjustmentReason,
    val note: String,
    val at: String,
)

enum class AdjustmentReason(val wire: String) {
    Recount("นับสต็อก"),
    Damaged("ยาเสียหาย"),
    Expired("ยาหมดอายุ"),
    Lost("สูญหาย"),
    Other("อื่นๆ");

    companion object {
        fun fromWire(value: String?): AdjustmentReason =
            entries.firstOrNull { it.wire == value } ?: Other

        val pickerOrder: List<AdjustmentReason> = listOf(Recount, Damaged, Expired, Lost, Other)
    }
}
