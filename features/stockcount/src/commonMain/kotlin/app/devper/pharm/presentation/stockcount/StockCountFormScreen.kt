package app.devper.pharm.presentation.stockcount

import androidx.compose.runtime.Composable
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
import app.devper.pharm.ui.i18n.pharmStrings
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StockCountFormScreen(
    onBack: () -> Unit,
    viewModel: StockCountFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = LocalPharmSnackbar.current
    val s = pharmStrings

    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.resetSaved()
            snackbar.showToast(PharmToast.Success(s.commonSaved))
            onBack()
        }
    }

    StockCountFormContent(
        state = state,
        callbacks = StockCountFormCallbacks(
            onSearchChange = viewModel::onQueryChange,
            onCountedChange = viewModel::onCountChange,
            onFillFromSystem = viewModel::requestFillFromSystem,
            onClearDraft = viewModel::requestClearDraft,
            onConfirmDraftAction = viewModel::confirmDraftAction,
            onCancelDraftAction = viewModel::cancelDraftAction,
            onSave = viewModel::requestSubmit,
            onConfirmSubmit = viewModel::confirmSubmit,
            onCancelSubmit = viewModel::cancelSubmit,
            onNotesChange = viewModel::onNoteChange,
            onBack = onBack,
            onDismissError = viewModel::dismissError,
        ),
    )
}
