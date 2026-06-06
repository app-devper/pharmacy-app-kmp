package app.devper.pharm.presentation.stock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DrugHistoryScreen(
    drugName: String,
    onBack: () -> Unit,
    viewModel: DrugHistoryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(drugName) { viewModel.load(drugName) }

    DrugHistoryContent(
        state = state,
        onBack = onBack,
        onDismissError = viewModel::dismissError,
    )
}
