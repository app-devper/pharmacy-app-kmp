package app.devper.pharm.presentation.planning

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.common.ReloadOnResume
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LowStockScreen(
    onOpenDrug: (drugId: String) -> Unit = {},
    viewModel: LowStockViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ReloadOnResume(viewModel::reload)
    LowStockContent(
        state = state,
        callbacks = LowStockCallbacks(
            onReload = viewModel::reload,
            onQueryChange = viewModel::onQueryChange,
            onRowClick = { onOpenDrug(it.id) },
            onDismissError = viewModel::dismissError,
        ),
    )
}
