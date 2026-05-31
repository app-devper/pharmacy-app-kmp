package app.devper.pharm.presentation.movements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MovementsScreen(viewModel: MovementsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = LocalPharmSnackbar.current

    LaunchedEffect(state.message) {
        state.message?.let {
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
