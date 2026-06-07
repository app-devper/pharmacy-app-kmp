package app.devper.pharm.domain.param

import kotlinx.datetime.LocalDate

data class AddKy9Param(
    val date: LocalDate,
    val drugName: String,
    val regNo: String = "",
    val unit: String,
    val qty: Int,
    val pricePerUnit: Double,
    val seller: String = "",
    val invoiceNo: String = "",
    val saleId: String = "",
)

data class KyMonthFilterParam(
    val month: String = "",
)

data class ExportKyFormParam(
    val form: String,
    val month: String = "",
)
