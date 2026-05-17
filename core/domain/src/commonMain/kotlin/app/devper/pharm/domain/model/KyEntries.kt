package app.devper.pharm.domain.model

data class Ky9Entry(
    val id: String,
    val saleId: String,
    val date: String,
    val drugName: String,
    val regNo: String,
    val unit: String,
    val qty: Int,
    val pricePerUnit: Double,
    val totalValue: Double,
    val seller: String,
    val invoiceNo: String,
    val createdAt: String,
)

data class Ky10Entry(
    val id: String,
    val saleId: String,
    val date: String,
    val drugName: String,
    val regNo: String,
    val qty: Int,
    val unit: String,
    val buyerName: String,
    val buyerAddress: String,
    val rxNo: String,
    val doctor: String,
    val balance: Int,
    val createdAt: String,
)

data class Ky11Entry(
    val id: String,
    val saleId: String,
    val date: String,
    val drugName: String,
    val regNo: String,
    val qty: Int,
    val unit: String,
    val buyerName: String,
    val purpose: String,
    val pharmacist: String,
    val createdAt: String,
)

data class Ky12Entry(
    val id: String,
    val saleId: String,
    val date: String,
    val rxNo: String,
    val patientName: String,
    val doctor: String,
    val hospital: String,
    val drugName: String,
    val qty: Int,
    val unit: String,
    val totalValue: Double,
    val status: String,
    val createdAt: String,
)

enum class KyFormType(val wire: String, val label: String) {
    Ky9("ky9", "ขย.9 (ซื้อยา)"),
    Ky10("ky10", "ขย.10 (ยาควบคุมพิเศษ)"),
    Ky11("ky11", "ขย.11 (ยาอันตราย)"),
    Ky12("ky12", "ขย.12 (ใบสั่งแพทย์)");
}
