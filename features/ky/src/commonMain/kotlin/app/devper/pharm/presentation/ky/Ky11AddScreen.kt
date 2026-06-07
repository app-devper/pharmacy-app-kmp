package app.devper.pharm.presentation.ky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Ky11AddScreen(
    onBack: () -> Unit,
    viewModel: Ky11AddViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.saved) { if (state.saved) onBack() }

    Ky11AddContent(
        state = state,
        callbacks = Ky11AddCallbacks(
            onBack = onBack,
            onDate = viewModel::onDate,
            onDrugName = viewModel::onDrugName,
            onRegNo = viewModel::onRegNo,
            onUnit = viewModel::onUnit,
            onQty = viewModel::onQty,
            onBuyerName = viewModel::onBuyerName,
            onPurpose = viewModel::onPurpose,
            onPharmacist = viewModel::onPharmacist,
            onSubmit = viewModel::submit,
            onDismissError = viewModel::dismissError,
        ),
    )
}
