package app.devper.pharm.presentation.ky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Ky9AddScreen(
    onBack: () -> Unit,
    viewModel: Ky9AddViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    Ky9AddContent(
        state = state,
        callbacks = Ky9AddCallbacks(
            onBack = onBack,
            onDate = viewModel::onDate,
            onDrugName = viewModel::onDrugName,
            onRegNo = viewModel::onRegNo,
            onUnit = viewModel::onUnit,
            onQty = viewModel::onQty,
            onPricePerUnit = viewModel::onPricePerUnit,
            onSeller = viewModel::onSeller,
            onInvoiceNo = viewModel::onInvoiceNo,
            onSubmit = viewModel::submit,
            onDismissError = viewModel::dismissError,
        ),
    )
}
