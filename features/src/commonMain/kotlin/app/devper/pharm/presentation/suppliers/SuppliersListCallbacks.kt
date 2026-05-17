package app.devper.pharm.presentation.suppliers

import app.devper.pharm.domain.model.Supplier

data class SuppliersListCallbacks(
    val onSearchChange: (String) -> Unit = {},
    val onOpenAdd: () -> Unit = {},
    val onOpenDetail: (Supplier) -> Unit = {},
    val onOpenEdit: (Supplier) -> Unit = {},
    val onRequestDelete: (Supplier) -> Unit = {},
    val onCancelDelete: () -> Unit = {},
    val onConfirmDelete: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
