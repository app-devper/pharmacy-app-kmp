package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.model.Customer

data class CustomersListCallbacks(
    val onSearchChange: (String) -> Unit = {},
    val onOpenDetail: (Customer) -> Unit = {},
    val onOpenEdit: (Customer) -> Unit = {},
    val onOpenAdd: () -> Unit = {},
    val onDelete: (Customer) -> Unit = {},
    val onDismissError: () -> Unit = {},
)
