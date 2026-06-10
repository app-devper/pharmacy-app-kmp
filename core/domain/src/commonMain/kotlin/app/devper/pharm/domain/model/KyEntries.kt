package app.devper.pharm.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class Ky9Entry(
    val id: String,
    val saleId: String,
    val date: LocalDate?,
    val drugName: String,
    val regNo: String,
    val unit: String,
    val qty: Int,
    val pricePerUnit: Double,
    val totalValue: Double,
    val seller: String,
    val invoiceNo: String,
    val createdAt: LocalDateTime?,
)

data class Ky10Entry(
    val id: String,
    val saleId: String,
    val date: LocalDate?,
    val drugName: String,
    val regNo: String,
    val qty: Int,
    val unit: String,
    val buyerName: String,
    val buyerAddress: String,
    val rxNo: String,
    val doctor: String,
    val balance: Int,
    val createdAt: LocalDateTime?,
)

data class Ky11Entry(
    val id: String,
    val saleId: String,
    val date: LocalDate?,
    val drugName: String,
    val regNo: String,
    val qty: Int,
    val unit: String,
    val buyerName: String,
    val purpose: String,
    val pharmacist: String,
    val createdAt: LocalDateTime?,
)

data class Ky12Entry(
    val id: String,
    val saleId: String,
    val date: LocalDate?,
    val rxNo: String,
    val patientName: String,
    val doctor: String,
    val hospital: String,
    val drugName: String,
    val qty: Int,
    val unit: String,
    val totalValue: Double,
    val status: String,
    val createdAt: LocalDateTime?,
)

enum class KyFormType(val wire: String) {
    Ky9("ky9"),
    Ky10("ky10"),
    Ky11("ky11"),
    Ky12("ky12");
}
