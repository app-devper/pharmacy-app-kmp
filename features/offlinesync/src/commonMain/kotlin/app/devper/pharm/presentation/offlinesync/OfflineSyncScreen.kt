package app.devper.pharm.presentation.offlinesync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OfflineSyncScreen(viewModel: OfflineSyncViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = LocalPharmSnackbar.current

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showToast(PharmToast.Success(it))
            viewModel.dismissMessage()
        }
    }

    OfflineSyncContent(
        state = state,
        callbacks = OfflineSyncCallbacks(
            onRefresh = viewModel::refresh,
            onSyncAll = viewModel::syncAll,
            onRetry = { viewModel.retry(it.id) },
            onCancel = { viewModel.askDiscard(it.id) },
            onConfirmCancel = viewModel::discardConfirmed,
            onDismissCancel = viewModel::cancelDiscard,
            onDismissError = viewModel::dismissError,
        ),
    )
}
