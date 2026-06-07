package app.devper.pharm.domain.param

import kotlinx.datetime.LocalDate

data class SaleHistoryFilterParam(

    val from: LocalDate? = null,

    val to: LocalDate? = null,

    val query: String? = null,
    val limit: Int = 200,
)

data class SubmitReturnParam(
    val saleId: String,
    val reason: String,
    val items: List<ReturnLineParam>,
)

data class ReturnLineParam(
    val saleItemId: String,

    val qty: Int,
)
