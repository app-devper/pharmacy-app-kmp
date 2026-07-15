package app.devper.pharm.presentation.ky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.ui.components.RegisterUnsavedChanges
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Ky10AddScreen(
    onBack: () -> Unit,
    viewModel: Ky10AddViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RegisterUnsavedChanges(state.hasUnsavedChanges)
    LaunchedEffect(state.saved) { if (state.saved) onBack() }

    Ky10AddContent(
        state = state,
        callbacks = Ky10AddCallbacks(
            onBack = onBack,
            onDate = viewModel::onDate,
            onDrugName = viewModel::onDrugName,
            onRegNo = viewModel::onRegNo,
            onUnit = viewModel::onUnit,
            onQty = viewModel::onQty,
            onBalance = viewModel::onBalance,
            onBuyerName = viewModel::onBuyerName,
            onBuyerAddress = viewModel::onBuyerAddress,
            onRxNo = viewModel::onRxNo,
            onDoctor = viewModel::onDoctor,
            onSubmit = viewModel::submit,
            onDismissError = viewModel::dismissError,
        ),
    )
}
