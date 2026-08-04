package app.devper.pharm.presentation.labels

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.LabelSize

data class LabelPrintCallbacks(
    val onQueryChange: (String) -> Unit = {},
    val onAddDrug: (Drug) -> Unit = {},
    val onRemoveLine: (Int) -> Unit = {},
    val onChangeCopies: (Int, Int) -> Unit = { _, _ -> },
    val onChangeBarcode: (Int, String) -> Unit = { _, _ -> },
    val onToggleIncludePrice: (Int, Boolean) -> Unit = { _, _ -> },
    val onSizeChange: (LabelSize) -> Unit = {},
    val onAskClearAll: () -> Unit = {},
    val onCancelClearAll: () -> Unit = {},
    val onConfirmClearAll: () -> Unit = {},
    val onPrint: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
