package app.devper.pharm.presentation.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.devper.pharm.presentation.suppliers.form.SupplierFormCallbacks
import app.devper.pharm.presentation.suppliers.form.SupplierFormContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SupplierFormScreen(
    supplierId: String?,
    onBack: () -> Unit,
    viewModel: SupplierFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(supplierId) {
        viewModel.init(
            if (supplierId.isNullOrBlank()) SupplierFormMode.Add
            else SupplierFormMode.Edit(supplierId),
        )
    }
    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.resetSaved()
            onBack()
        }
    }

    SupplierFormContent(
        state = state,
        callbacks = SupplierFormCallbacks(
            onSubmit = viewModel::submit,
            onBack = onBack,
            onDismissError = viewModel::dismissError,
            onName = viewModel::onName,
            onContactName = viewModel::onContactName,
            onPhone = viewModel::onPhone,
            onAddress = viewModel::onAddress,
            onTaxId = viewModel::onTaxId,
            onNotes = viewModel::onNotes,
        ),
    )
}
