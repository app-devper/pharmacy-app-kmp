package app.devper.pharm.presentation.movements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MovementsScreen(viewModel: MovementsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

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
