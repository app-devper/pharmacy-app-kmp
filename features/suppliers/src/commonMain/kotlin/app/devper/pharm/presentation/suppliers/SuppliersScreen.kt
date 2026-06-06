package app.devper.pharm.presentation.suppliers

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import app.devper.pharm.ui.common.ReloadOnResume
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SuppliersScreen(
    onAddSupplier: () -> Unit = {},
    onEditSupplier: (id: String) -> Unit = {},
    onOpenSupplierDetail: (id: String) -> Unit = onEditSupplier,
    viewModel: SuppliersListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReloadOnResume(viewModel::reload)
    val callbacks = remember(viewModel, onAddSupplier, onEditSupplier, onOpenSupplierDetail) {
        SuppliersListCallbacks(
            onSearchChange = viewModel::onQueryChange,
            onOpenAdd = onAddSupplier,
            onOpenDetail = { onOpenSupplierDetail(it.id) },
            onOpenEdit = { onEditSupplier(it.id) },
            onRequestDelete = viewModel::confirmDelete,
            onCancelDelete = viewModel::cancelDelete,
            onConfirmDelete = viewModel::deleteConfirmed,
            onDismissError = viewModel::dismissError,
        )
    }

    SuppliersListContent(state = state, callbacks = callbacks)
}
