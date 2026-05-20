package app.devper.pharm.presentation.stockcount

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StockCountFormScreen(
    onBack: () -> Unit,
    viewModel: StockCountFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.resetSaved()
            onBack()
        }
    }

    StockCountFormContent(
        state = state,
        callbacks = StockCountFormCallbacks(
            onSearchChange = viewModel::onQueryChange,
            onCountedChange = viewModel::onCountChange,
            onFillFromSystem = viewModel::onFillFromSystem,
            onClear = viewModel::onClear,
            onClearDraft = viewModel::onClearDraft,
            onSave = viewModel::requestSubmit,
            onConfirmSubmit = viewModel::confirmSubmit,
            onCancelSubmit = viewModel::cancelSubmit,
            onNotesChange = viewModel::onNoteChange,
            onBack = onBack,
            onDismissError = viewModel::dismissError,
        ),
    )
}
