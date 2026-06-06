package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.model.Ky9Entry
import app.devper.pharm.ui.common.BaseUiState

data class Ky9Draft(
    val date: String = "",
    val drugName: String = "",
    val regNo: String = "",
    val unit: String = "",
    val qty: String = "",
    val pricePerUnit: String = "",
    val seller: String = "",
    val invoiceNo: String = "",
)

data class Ky9UiState(
    val month: String = "",
    override val loading: Boolean = false,
    val entries: List<Ky9Entry> = emptyList(),
    val exporting: Boolean = false,
    val message: String? = null,
    override val error: String? = null,
) : BaseUiState
