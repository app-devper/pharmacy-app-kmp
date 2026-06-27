package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
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
            snackbar.showToast(PharmToast.Info(it.localize(s)))
            viewModel.dismissMessage()
        }
    }

    ReloadOnResume(viewModel::reload)

    ProfitContent(
        state = state,
        callbacks = ProfitCallbacks(
            onFromMillisChange = viewModel::onFromMillisChange,
            onToMillisChange = viewModel::onToMillisChange,
            onSortChange = viewModel::onSort,
            onExportExcel = viewModel::onExportExcel,
            onDismissError = viewModel::dismissError,
        ),
    )
}
