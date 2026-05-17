package app.devper.pharm.domain.model

data class StockMovement(
    val id: String,
    val type: MovementType,
    val drugId: String,
    val drugName: String,

    val delta: Int,

    val reference: String,
    val note: String,
    val at: String,
)

enum class MovementType(val wire: String, val label: String) {
    Import("import", "นำเข้า"),
    Sale("sale", "ขาย"),
    Return("return", "คืน"),
    Adjustment("adjustment", "ปรับสต็อก"),
    Writeoff("writeoff", "ตัดจำหน่าย");

    companion object {
        fun fromWire(s: String): MovementType? = values().firstOrNull { it.wire == s }
    }
}

data class StockMovementsPage(
    val items: List<StockMovement>,
    val total: Int,
)
