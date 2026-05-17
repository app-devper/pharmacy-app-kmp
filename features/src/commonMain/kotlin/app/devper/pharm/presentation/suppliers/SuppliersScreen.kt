package app.devper.pharm.presentation.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.devper.pharm.domain.model.Supplier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SuppliersScreen(
    onAddSupplier: () -> Unit = {},
    onEditSupplier: (id: String) -> Unit = {},
    onOpenSupplierDetail: (id: String) -> Unit = onEditSupplier,
    viewModel: SuppliersListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val byId: (Supplier) -> String = { it.id }

    SuppliersListContent(
        state = state,
        callbacks = SuppliersListCallbacks(
            onSearchChange = viewModel::onQueryChange,
            onOpenAdd = onAddSupplier,
            onOpenDetail = { onOpenSupplierDetail(byId(it)) },
            onOpenEdit = { onEditSupplier(byId(it)) },
            onRequestDelete = viewModel::confirmDelete,
            onCancelDelete = viewModel::cancelDelete,
            onConfirmDelete = viewModel::deleteConfirmed,
            onDismissError = viewModel::dismissError,
        ),
    )
}
