package app.devper.pharm.presentation.ky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.ui.common.ReloadOnResume
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Ky9Screen(
    onSwitchForm: (KyFormType) -> Unit = {},
    onAddEntry: () -> Unit = {},
    viewModel: Ky9ViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReloadOnResume(viewModel::reload)

    Ky9Content(
        state = state,
        callbacks = Ky9Callbacks(
            onSwitchForm = onSwitchForm,
            onMonthChange = viewModel::onMonthChange,
            onApply = viewModel::applyFilter,
            onExport = viewModel::exportPdf,
            onAddEntry = onAddEntry,
            onDismissMessage = viewModel::dismissMessage,
            onDismissError = viewModel::dismissError,
        ),
    )
}
