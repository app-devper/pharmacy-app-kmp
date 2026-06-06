package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EodScreen(onBack: () -> Unit = {}, viewModel: EodViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    EodContent(
        state = state,
        onBack = onBack,
        callbacks = EodCallbacks(
            onDateChange = viewModel::onDateChange,
            onApplyDate = viewModel::applyDate,
            onRequestClose = viewModel::requestCloseDay,
            onConfirmClose = viewModel::confirmCloseDay,
            onCancelClose = viewModel::cancelCloseDay,
            onPrint = viewModel::printReceipt,
            onDismissError = viewModel::dismissError,
        ),
    )
}
