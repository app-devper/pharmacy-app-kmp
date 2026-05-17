package app.devper.pharm.presentation.offlinesync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OfflineSyncScreen(viewModel: OfflineSyncViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    OfflineSyncContent(
        state = state,
        callbacks = OfflineSyncCallbacks(
            onRefresh = viewModel::refresh,
            onSyncAll = viewModel::syncAll,
            onRetry = { viewModel.retry(it.id) },
            onCancel = { viewModel.askDiscard(it.id) },
            onConfirmCancel = viewModel::discardConfirmed,
            onDismissCancel = viewModel::cancelDiscard,
            onDismissMessage = viewModel::dismissMessage,
            onDismissError = viewModel::dismissError,
        ),
    )
}
