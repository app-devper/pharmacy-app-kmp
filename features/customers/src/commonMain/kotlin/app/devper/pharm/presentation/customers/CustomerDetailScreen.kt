package app.devper.pharm.presentation.customers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CustomerDetailScreen(
    customerId: String,
    onBack: () -> Unit,
    onEdit: (id: String) -> Unit,
    viewModel: CustomerDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(customerId) { viewModel.load(customerId) }

    CustomerDetailContent(
        state = state,
        callbacks = CustomerDetailCallbacks(
            onBack = onBack,
            onEdit = { onEdit(customerId) },
            onDismissError = viewModel::dismissError,
        ),
    )
}
