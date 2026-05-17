package app.devper.pharm.presentation.stockcount

import app.devper.pharm.domain.model.StockCount

data class StockCountsListCallbacks(
    val onSearchChange: (String) -> Unit = {},
    val onNewCount: () -> Unit = {},
    val onOpenDetail: (StockCount) -> Unit = {},
    val onEdit: (StockCount) -> Unit = {},
    val onDelete: (StockCount) -> Unit = {},
    val onDismissError: () -> Unit = {},
)
