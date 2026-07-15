package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.common.ReloadOnResume
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReportsScreen(
    onCloseEod: () -> Unit = {},
    viewModel: ReportsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ReloadOnResume(viewModel::reload)
    ReportsContent(
        state = state,
        callbacks = ReportsCallbacks(
            onSelectWindow = viewModel::selectWindow,
            onReload = viewModel::reload,
            onCloseEod = onCloseEod,
            onDismissError = viewModel::dismissError,
        ),
    )
}
