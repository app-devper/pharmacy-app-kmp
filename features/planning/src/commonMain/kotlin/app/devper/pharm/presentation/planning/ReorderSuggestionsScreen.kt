package app.devper.pharm.presentation.planning

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.common.ReloadOnResume
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReorderSuggestionsScreen(
    onBack: () -> Unit,
    viewModel: ReorderSuggestionsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ReloadOnResume(viewModel::reload)
    ReorderSuggestionsContent(
        state = state,
        onBack = onBack,
        callbacks = ReorderSuggestionsCallbacks(
            onReload = viewModel::reload,
            onDismissError = viewModel::dismissError,
        ),
    )
}
