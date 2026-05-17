package app.devper.pharm.presentation.help

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HelpScreen(viewModel: HelpViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    HelpContent(
        state = state,
        callbacks = HelpCallbacks(
            onDismissError = viewModel::dismissError,
        ),
    )
}
