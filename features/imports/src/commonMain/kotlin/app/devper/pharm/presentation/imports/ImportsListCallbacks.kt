package app.devper.pharm.presentation.imports

import app.devper.pharm.domain.model.PurchaseOrderSummary

data class ImportsListCallbacks(
    val onSearchChange: (String) -> Unit = {},
    val onCreateImport: () -> Unit = {},
    val onOpenImport: (PurchaseOrderSummary) -> Unit = {},
    val onEdit: (PurchaseOrderSummary) -> Unit = {},
    val onRequestConfirm: (PurchaseOrderSummary) -> Unit = {},
    val onCancelConfirm: () -> Unit = {},
    val onConfirmConfirm: () -> Unit = {},
    val onRequestDelete: (PurchaseOrderSummary) -> Unit = {},
    val onCancelDelete: () -> Unit = {},
    val onConfirmDelete: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
