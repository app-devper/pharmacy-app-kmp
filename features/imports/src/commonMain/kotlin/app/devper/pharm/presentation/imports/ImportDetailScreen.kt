package app.devper.pharm.presentation.imports

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ImportDetailScreen(
    importId: String,
    onBack: () -> Unit,
    onEdit: (id: String) -> Unit,
    viewModel: ImportDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(importId) { viewModel.init(importId) }
    LaunchedEffect(state.closed) { if (state.closed) onBack() }

    ImportDetailContent(
        state = state,
        callbacks = ImportDetailCallbacks(
            onBack = onBack,
            onEdit = onEdit,
            onAskConfirm = viewModel::askConfirm,
            onAskDelete = viewModel::askDelete,
            onConfirmNow = viewModel::confirmNow,
            onCancelConfirm = viewModel::cancelConfirm,
            onDeleteNow = viewModel::deleteNow,
            onCancelDelete = viewModel::cancelDelete,
            onDismissError = viewModel::dismissError,
        ),
    )
}
