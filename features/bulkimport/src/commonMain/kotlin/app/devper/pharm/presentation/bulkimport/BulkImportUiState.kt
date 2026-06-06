package app.devper.pharm.presentation.bulkimport

import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.param.AddDrugParam
import app.devper.pharm.ui.common.BaseUiState

data class BulkImportUiState(
    val text: String = "",
    val parsed: List<AddDrugParam> = emptyList(),
    val previewCount: Int? = null,
    val parseError: String? = null,
    val submitting: Boolean = false,
    val result: BulkImportResult? = null,
    override val error: String? = null,
) : BaseUiState {

    override val loading: Boolean get() = submitting

    val canSubmit: Boolean get() = !submitting && text.isNotBlank()

    val rows: List<BulkImportRow> = run {
        val errorByRow = result?.errors.orEmpty().associateBy { it.row }
        parsed.mapIndexed { idx, p ->
            val rowNo = idx + 1
            val err = errorByRow[rowNo]
            BulkImportRow(
                row = rowNo,
                name = p.name,
                qty = p.stock,
                unit = p.unit,
                status = when {
                    err != null    -> BulkImportRowStatus.Failed
                    result != null -> BulkImportRowStatus.Done
                    else           -> BulkImportRowStatus.Pending
                },
                errorMessage = err?.message,
            )
        }
    }
}

enum class BulkImportRowStatus { Pending, Done, Failed }

data class BulkImportRow(
    val row: Int,
    val name: String,
    val qty: Int,
    val unit: String,
    val status: BulkImportRowStatus,
    val errorMessage: String?,
)
