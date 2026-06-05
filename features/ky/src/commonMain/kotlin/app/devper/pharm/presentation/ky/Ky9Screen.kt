package app.devper.pharm.presentation.ky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.domain.model.KyFormType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Ky9Screen(
    onSwitchForm: (KyFormType) -> Unit = {},
    viewModel: Ky9ViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Ky9Content(
        state = state,
        callbacks = Ky9Callbacks(
            onSwitchForm = onSwitchForm,
            onMonthChange = viewModel::onMonthChange,
            onApply = viewModel::applyFilter,
            onExport = viewModel::exportPdf,
            onToggleAddForm = viewModel::toggleAddForm,
            onDate = viewModel::onDate,
            onDrugName = viewModel::onDrugName,
            onRegNo = viewModel::onRegNo,
            onUnit = viewModel::onUnit,
            onQty = viewModel::onQty,
            onPricePerUnit = viewModel::onPricePerUnit,
            onSeller = viewModel::onSeller,
            onInvoiceNo = viewModel::onInvoiceNo,
            onSubmitAdd = viewModel::submitAdd,
            onDismissMessage = viewModel::dismissMessage,
            onDismissError = viewModel::dismissError,
        ),
    )
}
