package app.devper.pharm.presentation.help

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HelpScreen(viewModel: HelpViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HelpContent(
        state = state,
        callbacks = HelpCallbacks(
            onDismissError = viewModel::dismissError,
        ),
    )
}
