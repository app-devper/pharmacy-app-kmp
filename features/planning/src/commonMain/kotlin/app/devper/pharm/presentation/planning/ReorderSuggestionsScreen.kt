package app.devper.pharm.presentation.planning

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReorderSuggestionsScreen(viewModel: ReorderSuggestionsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ReorderSuggestionsContent(
        state = state,
        callbacks = ReorderSuggestionsCallbacks(
            onReload = viewModel::reload,
            onDismissError = viewModel::dismissError,
        ),
    )
}
