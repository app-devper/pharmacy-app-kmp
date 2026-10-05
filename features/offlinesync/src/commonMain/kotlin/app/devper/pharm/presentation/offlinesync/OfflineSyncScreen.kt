package app.devper.pharm.presentation.offlinesync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.presentation.offlinesync.i18n.localize
import app.devper.pharm.presentation.offlinesync.message.OfflineSyncUiStateMessage
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
import app.devper.pharm.ui.i18n.pharmStrings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OfflineSyncScreen(viewModel: OfflineSyncViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = LocalPharmSnackbar.current
    val s = pharmStrings

    LaunchedEffect(state.messageState) {
        state.messageState?.let {
            val toast = when (it) {
                OfflineSyncUiStateMessage.Recorded,
                OfflineSyncUiStateMessage.Abandoned,
                OfflineSyncUiStateMessage.Discarded,
                is OfflineSyncUiStateMessage.Exported -> PharmToast.Success(it.localize(s))
                else -> PharmToast.Info(it.localize(s))
            }
            snackbar.showToast(toast)
            viewModel.dismissMessage()
        }
    }

    OfflineSyncContent(
        state = state,
        callbacks = OfflineSyncCallbacks(
            onSyncAll = viewModel::syncAll,
            onRetry = { viewModel.retry(it.id) },
            onAbandon = { viewModel.askAbandon(it.id) },
            onExport = { viewModel.export(it.id) },
            onDiscard = { viewModel.askDiscard(it.id) },
            onReasonChange = viewModel::reasonChanged,
            onConfirmResolving = viewModel::confirmResolving,
            onDismissResolving = viewModel::cancelResolving,
            onDismissError = viewModel::dismissError,
        ),
    )
}
