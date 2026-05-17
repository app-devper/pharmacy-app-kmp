package app.devper.pharm.presentation.bulkimport

data class BulkImportCallbacks(
    val onJsonChange: (String) -> Unit = {},
    val onPickFile: () -> Unit = {},
    val onDownloadTemplate: () -> Unit = {},
    val onPreview: () -> Unit = {},
    val onSubmit: () -> Unit = {},
    val onClear: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
