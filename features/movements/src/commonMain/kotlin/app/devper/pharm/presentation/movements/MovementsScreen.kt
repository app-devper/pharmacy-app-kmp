package app.devper.pharm.presentation.movements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
import app.devper.pharm.ui.common.ReloadOnResume
import app.devper.pharm.ui.i18n.localize
import app.devper.pharm.ui.i18n.pharmStrings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MovementsScreen(viewModel: MovementsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReloadOnResume(viewModel::applyFilter)
    val snackbar = LocalPharmSnackbar.current
    val s = pharmStrings

    LaunchedEffect(state.message, state.messageState) {
        val text = state.message ?: state.messageState?.localize(s)
        text?.let {
            snackbar.showToast(PharmToast.Info(it))
            viewModel.dismissMessage()
        }
    }

    MovementsContent(
        state = state,
        callbacks = MovementsCallbacks(
            onSearchChange = viewModel::onSearchChange,
            onFromMillisChange = viewModel::onFromMillisChange,
            onToMillisChange = viewModel::onToMillisChange,
            onApplyFilter = viewModel::applyFilter,
            onToggleType = viewModel::onToggleType,
            onPrevPage = viewModel::onPrevPage,
            onNextPage = viewModel::onNextPage,
            onExportExcel = viewModel::onExportExcel,
            onDismissError = viewModel::dismissError,
        ),
    )
}
