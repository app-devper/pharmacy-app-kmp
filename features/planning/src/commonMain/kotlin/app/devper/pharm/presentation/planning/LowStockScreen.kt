package app.devper.pharm.presentation.planning

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LowStockScreen(viewModel: LowStockViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    LowStockContent(
        state = state,
        callbacks = LowStockCallbacks(
            onReload = viewModel::reload,
            onDismissError = viewModel::dismissError,
        ),
    )
}
