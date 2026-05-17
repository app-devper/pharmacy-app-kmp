package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.model.Ky9Entry
import app.devper.pharm.domain.parser.Ky9DraftBuilder
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
    val addFormOpen: Boolean = false,
    val draft: Ky9Draft = Ky9Draft(),
    val saving: Boolean = false,
    val exporting: Boolean = false,
    val message: String? = null,
    override val error: String? = null,
) : BaseUiState {
    val canSubmitDraft: Boolean
        get() = !saving && Ky9DraftBuilder.isDraftValid(
            date = draft.date,
            drugName = draft.drugName,
            unit = draft.unit,
            qty = draft.qty,
            pricePerUnit = draft.pricePerUnit,
        )
}
