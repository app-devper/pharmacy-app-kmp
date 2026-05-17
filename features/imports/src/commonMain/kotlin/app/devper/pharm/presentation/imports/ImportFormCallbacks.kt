package app.devper.pharm.presentation.imports

import app.devper.pharm.domain.model.Drug

data class ImportFormCallbacks(
    val onSupplier: (String) -> Unit = {},
    val onInvoiceNo: (String) -> Unit = {},
    val onReceiveDate: (String) -> Unit = {},
    val onNotes: (String) -> Unit = {},
    val onAddLine: () -> Unit = {},
    val onRemoveLine: (Int) -> Unit = {},
    val onLineDrug: (Int, Drug) -> Unit = { _, _ -> },
    val onLineLotNumber: (Int, String) -> Unit = { _, _ -> },
    val onLineExpiry: (Int, String) -> Unit = { _, _ -> },
    val onLineQty: (Int, String) -> Unit = { _, _ -> },
    val onLineCost: (Int, String) -> Unit = { _, _ -> },
    val onLineSell: (Int, String) -> Unit = { _, _ -> },
    val onSubmit: () -> Unit = {},
    val onBack: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
