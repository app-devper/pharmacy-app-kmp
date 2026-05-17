package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReportsScreen(
    onCloseEod: () -> Unit = {},
    viewModel: ReportsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    ReportsContent(
        state = state,
        callbacks = ReportsCallbacks(
            onSelectWindow = viewModel::selectWindow,
            onCloseEod = onCloseEod,
            onDismissError = viewModel::dismissError,
        ),
    )
}
