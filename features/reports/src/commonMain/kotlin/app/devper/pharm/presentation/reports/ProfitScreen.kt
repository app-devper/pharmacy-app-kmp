package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.toToast
import app.devper.pharm.ui.i18n.localize
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.common.ReloadOnResume
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfitScreen(viewModel: ProfitViewModel = koinViewModel()) {
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

    ProfitContent(
        state = state,
        callbacks = ProfitCallbacks(
            onDateRangeChange = viewModel::onDateRangeChange,
            onSortChange = viewModel::onSort,
            onExportExcel = viewModel::onExportExcel,
            onDismissError = viewModel::dismissError,
        ),
    )
}
