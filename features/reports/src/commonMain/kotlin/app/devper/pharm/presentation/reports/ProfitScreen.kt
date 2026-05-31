package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfitScreen(viewModel: ProfitViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = LocalPharmSnackbar.current

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showToast(PharmToast.Info(it))
            viewModel.dismissMessage()
        }
    }

    ProfitContent(
        state = state,
        callbacks = ProfitCallbacks(
            onFromMillisChange = viewModel::onFromMillisChange,
            onToMillisChange = viewModel::onToMillisChange,
            onQuickPeriod = viewModel::onQuickPeriod,
            onSortChange = viewModel::onSort,
            onApplyRange = viewModel::applyRange,
            onExportExcel = viewModel::onExportExcel,
            onDismissError = viewModel::dismissError,
        ),
    )
}
