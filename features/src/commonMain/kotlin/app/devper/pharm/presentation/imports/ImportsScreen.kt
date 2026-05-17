package app.devper.pharm.presentation.imports

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.devper.pharm.domain.model.PurchaseOrderSummary
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ImportsScreen(
    onAddImport: () -> Unit = {},
    onOpenImport: (id: String) -> Unit = {},
    onEditImport: (id: String) -> Unit = onOpenImport,
    viewModel: ImportsListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val byId: (PurchaseOrderSummary) -> String = { it.id }

    ImportsListContent(
        state = state,
        callbacks = ImportsListCallbacks(
            onSearchChange = viewModel::onQueryChange,
            onCreateImport = onAddImport,
            onOpenImport = { onOpenImport(byId(it)) },
            onEdit = { onEditImport(byId(it)) },
            onRequestConfirm = viewModel::requestConfirm,
            onCancelConfirm = viewModel::cancelConfirm,
            onConfirmConfirm = viewModel::confirmConfirmed,
            onRequestDelete = viewModel::requestDelete,
            onCancelDelete = viewModel::cancelDelete,
            onConfirmDelete = viewModel::deleteConfirmed,
            onDismissError = viewModel::dismissError,
        ),
    )
}
