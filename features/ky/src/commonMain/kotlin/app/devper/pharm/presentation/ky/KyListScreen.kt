package app.devper.pharm.presentation.ky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.devper.pharm.domain.model.KyFormType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun KyListScreen(
    formType: KyFormType,
    onSwitchForm: (KyFormType) -> Unit = {},
    viewModel: KyListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(formType) { viewModel.init(formType) }

    KyListContent(
        state = state,
        callbacks = KyListCallbacks(
            onSwitchForm = onSwitchForm,
            onMonthChange = viewModel::onMonthChange,
            onApply = viewModel::applyFilter,
            onExport = viewModel::exportPdf,
            onDismissMessage = viewModel::dismissMessage,
            onDismissError = viewModel::dismissError,
        ),
    )
}
