package app.devper.pharm.presentation.ky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.toToast
import app.devper.pharm.ui.common.ReloadOnResume
import app.devper.pharm.ui.i18n.localize
import app.devper.pharm.ui.i18n.pharmStrings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun KyListScreen(
    formType: KyFormType,
    onSwitchForm: (KyFormType) -> Unit = {},
    onAddEntry: () -> Unit = {},
    viewModel: KyListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = LocalPharmSnackbar.current
    val s = pharmStrings

    LaunchedEffect(formType) { viewModel.init(formType) }
    ReloadOnResume(viewModel::reload)

    LaunchedEffect(state.messageState) {
        state.messageState?.let {
            snackbar.showToast(it.toToast(s))
            viewModel.dismissMessage()
        }
    }

    KyListContent(
        state = state,
        callbacks = KyListCallbacks(
            onSwitchForm = onSwitchForm,
            onMonthChange = viewModel::onMonthChange,
            onApply = viewModel::applyFilter,
            onExport = viewModel::exportPdf,
            onAddEntry = onAddEntry,
            onDismissError = viewModel::dismissError,
        ),
    )
}
