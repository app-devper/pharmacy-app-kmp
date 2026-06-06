package app.devper.pharm.presentation.ky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Ky12AddScreen(
    onBack: () -> Unit,
    viewModel: Ky12AddViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.saved) { if (state.saved) onBack() }

    Ky12AddContent(
        state = state,
        callbacks = Ky12AddCallbacks(
            onBack = onBack,
            onDate = viewModel::onDate,
            onDrugName = viewModel::onDrugName,
            onRegNo = viewModel::onRegNo,
            onUnit = viewModel::onUnit,
            onQty = viewModel::onQty,
            onTotalValue = viewModel::onTotalValue,
            onRxNo = viewModel::onRxNo,
            onPatientName = viewModel::onPatientName,
            onDoctor = viewModel::onDoctor,
            onHospital = viewModel::onHospital,
            onStatus = viewModel::onStatus,
            onSubmit = viewModel::submitAdd,
            onDismissError = viewModel::dismissError,
        ),
    )
}
