package app.devper.pharm.presentation.planning

import app.devper.pharm.domain.model.Drug

data class LowStockCallbacks(
    val onReload: () -> Unit = {},
    val onRowClick: (Drug) -> Unit = {},
    val onDismissError: () -> Unit = {},
)
