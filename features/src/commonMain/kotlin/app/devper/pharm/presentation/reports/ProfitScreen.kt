package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfitScreen(viewModel: ProfitViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

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
