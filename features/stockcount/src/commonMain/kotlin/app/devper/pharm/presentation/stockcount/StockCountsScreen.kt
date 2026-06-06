package app.devper.pharm.presentation.stockcount

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.common.ReloadOnResume
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StockCountsScreen(
    onNewCount: () -> Unit = {},
    onOpenCount: (id: String) -> Unit = {},
    onEditCount: (id: String) -> Unit = {},
    onDeleteCount: (id: String) -> Unit = {},
    viewModel: StockCountsListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReloadOnResume(viewModel::reload)

    StockCountsListContent(
        state = state,
        callbacks = StockCountsListCallbacks(
            onSearchChange = viewModel::onQueryChange,
            onNewCount = onNewCount,
            onOpenDetail = { onOpenCount(it.id) },
            onEdit = { onEditCount(it.id) },
            onDelete = { onDeleteCount(it.id) },
            onDismissError = viewModel::dismissError,
        ),
    )
}
