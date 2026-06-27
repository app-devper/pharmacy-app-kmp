package app.devper.pharm.presentation.ky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
import app.devper.pharm.ui.common.ReloadOnResume
import app.devper.pharm.ui.i18n.localize
import app.devper.pharm.ui.i18n.pharmStrings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Ky9Screen(
    onSwitchForm: (KyFormType) -> Unit = {},
    onAddEntry: () -> Unit = {},
    viewModel: Ky9ViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = LocalPharmSnackbar.current
    val s = pharmStrings

    ReloadOnResume(viewModel::reload)

    LaunchedEffect(state.messageState) {
        state.messageState?.let {
            snackbar.showToast(PharmToast.Info(it.localize(s)))
            viewModel.dismissMessage()
        }
    }

    Ky9Content(
        state = state,
        callbacks = Ky9Callbacks(
            onSwitchForm = onSwitchForm,
            onMonthChange = viewModel::onMonthChange,
            onApply = viewModel::applyFilter,
            onExport = viewModel::exportPdf,
            onAddEntry = onAddEntry,
            onDismissError = viewModel::dismissError,
        ),
    )
}
