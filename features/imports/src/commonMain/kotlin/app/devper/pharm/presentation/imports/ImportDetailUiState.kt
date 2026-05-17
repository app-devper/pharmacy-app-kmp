package app.devper.pharm.presentation.imports

import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.ui.common.BaseUiState

data class ImportDetailUiState(
    override val loading: Boolean = false,
    val confirming: Boolean = false,
    val deleting: Boolean = false,
    val po: PurchaseOrder? = null,
    val confirmDialog: Boolean = false,
    val deleteDialog: Boolean = false,
    val closed: Boolean = false,
    override val error: String? = null,
) : BaseUiState
