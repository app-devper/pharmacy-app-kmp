package app.devper.pharm.presentation.bulkimport

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BulkImportScreen(
    onDownloadTemplate: () -> Unit = {},
    onPickFile: () -> Unit = {},
    viewModel: BulkImportViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    BulkImportContent(
        state = state,
        callbacks = BulkImportCallbacks(
            onJsonChange = viewModel::onTextChange,
            onPickFile = onPickFile,
            onDownloadTemplate = onDownloadTemplate,
            onPreview = viewModel::preview,
            onSubmit = viewModel::submit,
            onClear = viewModel::reset,
            onDismissError = viewModel::dismissError,
        ),
    )
}
