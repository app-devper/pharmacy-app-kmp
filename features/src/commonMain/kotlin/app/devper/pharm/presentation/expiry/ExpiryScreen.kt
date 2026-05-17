package app.devper.pharm.presentation.expiry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExpiryScreen(
    onExportExcel: () -> Unit = {},
    viewModel: ExpiryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    ExpiryContent(
        state = state,
        callbacks = ExpiryCallbacks(
            onWindowChange = viewModel::selectWindow,
            onToggleRow = viewModel::toggleSelected,
            onToggleAll = viewModel::toggleAll,
            onClearSelection = viewModel::clearSelection,
            onAskWriteoff = viewModel::askConfirm,
            onConfirmWriteoff = viewModel::confirmWriteoff,
            onCancelWriteoff = viewModel::cancelConfirm,
            onDismissResult = viewModel::dismissResult,
            onExportExcel = onExportExcel,
            onDismissError = viewModel::dismissError,
        ),
    )
}
