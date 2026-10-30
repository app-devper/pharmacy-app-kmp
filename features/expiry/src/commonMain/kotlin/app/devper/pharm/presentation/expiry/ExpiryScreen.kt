package app.devper.pharm.presentation.expiry

import androidx.compose.runtime.LaunchedEffect
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.toToast
import app.devper.pharm.ui.i18n.pharmStrings
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.common.ReloadOnResume
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExpiryScreen(
    viewModel: ExpiryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = LocalPharmSnackbar.current
    val s = pharmStrings

    LaunchedEffect(state.messageState) {
        state.messageState?.let {
            snackbar.showToast(it.toToast(s))
            viewModel.dismissMessage()
        }
    }

    ReloadOnResume(viewModel::reload)

    ExpiryContent(
        state = state,
        callbacks = ExpiryCallbacks(
            onWindowChange = viewModel::selectWindow,
            onQueryChange = viewModel::onQueryChange,
            onToggleRow = viewModel::toggleSelected,
            onToggleAll = viewModel::toggleAll,
            onClearSelection = viewModel::clearSelection,
            onAskWriteoff = viewModel::askConfirm,
            onConfirmWriteoff = viewModel::confirmWriteoff,
            onCancelWriteoff = viewModel::cancelConfirm,
            onDismissResult = viewModel::dismissResult,
            onExportExcel = viewModel::onExportExcel,
            onDismissError = viewModel::dismissError,
            onReload = viewModel::reload,
        ),
    )
}
