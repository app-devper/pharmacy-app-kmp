package app.devper.pharm.presentation.imports

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.components.RegisterUnsavedChanges
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ImportFormScreen(
    importId: String?,
    onBack: () -> Unit,
    viewModel: ImportFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RegisterUnsavedChanges(state.hasUnsavedChanges)

    LaunchedEffect(importId) {
        viewModel.init(
            if (importId.isNullOrBlank()) ImportFormMode.Add
            else ImportFormMode.Edit(importId),
        )
    }
    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.resetSaved()
            onBack()
        }
    }

    ImportFormContent(
        state = state,
        callbacks = ImportFormCallbacks(
            onSupplier = viewModel::onSupplier,
            onInvoiceNo = viewModel::onInvoiceNo,
            onReceiveDate = viewModel::onReceiveDate,
            onNotes = viewModel::onNotes,
            onAddLine = viewModel::addLine,
            onRemoveLine = viewModel::removeLine,
            onLineDrug = viewModel::onLineDrug,
            onLineLotNumber = viewModel::onLineLotNumber,
            onLineExpiry = viewModel::onLineExpiry,
            onLineQty = viewModel::onLineQty,
            onLineCost = viewModel::onLineCost,
            onLineSell = viewModel::onLineSell,
            onSubmit = viewModel::submit,
            onBack = onBack,
            onDismissError = viewModel::dismissError,
        ),
    )
}
