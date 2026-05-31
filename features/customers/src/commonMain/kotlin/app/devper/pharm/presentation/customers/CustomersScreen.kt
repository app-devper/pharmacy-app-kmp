package app.devper.pharm.presentation.customers

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CustomersScreen(
    onAddCustomer: () -> Unit = {},
    onOpenCustomer: (id: String) -> Unit = {},
    onEditCustomer: (id: String) -> Unit = {},
    viewModel: CustomersListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CustomersListContent(
        state = state,
        callbacks = CustomersListCallbacks(
            onSearchChange = viewModel::onQueryChange,
            onOpenDetail = { onOpenCustomer(it.id) },
            onOpenEdit = { onEditCustomer(it.id) },
            onOpenAdd = onAddCustomer,
            onDismissError = viewModel::dismissError,
        ),
    )
}
