package app.devper.pharm.presentation.labels

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.common.ReloadOnResume
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LabelPrintScreen(viewModel: LabelPrintViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ReloadOnResume(viewModel::reload)
    LabelPrintContent(
        state = state,
        callbacks = LabelPrintCallbacks(
            onQueryChange = viewModel::onQueryChange,
            onAddDrug = viewModel::onAddDrug,
            onRemoveLine = viewModel::onRemoveLine,
            onChangeCopies = viewModel::onChangeCopies,
            onChangeBarcode = viewModel::onChangeBarcode,
            onToggleIncludePrice = viewModel::onToggleIncludePrice,
            onSizeChange = viewModel::onSizeChange,
            onClearAll = viewModel::onClearAll,
            onPrint = viewModel::onPrint,
            onDismissMessage = viewModel::dismissMessage,
            onDismissError = viewModel::dismissError,
        ),
    )
}
