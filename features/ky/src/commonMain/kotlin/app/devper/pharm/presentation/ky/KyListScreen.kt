package app.devper.pharm.presentation.ky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.ui.common.ReloadOnResume
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun KyListScreen(
    formType: KyFormType,
    onSwitchForm: (KyFormType) -> Unit = {},
    onAddEntry: () -> Unit = {},
    viewModel: KyListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(formType) { viewModel.init(formType) }

    ReloadOnResume(viewModel::reload)

    KyListContent(
        state = state,
        callbacks = KyListCallbacks(
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
