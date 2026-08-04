package app.devper.pharm.presentation.suppliers

import androidx.compose.runtime.Composable
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
import app.devper.pharm.ui.i18n.pharmStrings
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.presentation.suppliers.form.SupplierFormCallbacks
import app.devper.pharm.presentation.suppliers.form.SupplierFormContent
import app.devper.pharm.ui.components.RegisterUnsavedChanges
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SupplierFormScreen(
    supplierId: String?,
    onBack: () -> Unit,
    viewModel: SupplierFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = LocalPharmSnackbar.current
    val s = pharmStrings
    RegisterUnsavedChanges(state.hasUnsavedChanges)

    LaunchedEffect(supplierId) {
        viewModel.init(
            if (supplierId.isNullOrBlank()) SupplierFormMode.Add
            else SupplierFormMode.Edit(supplierId),
        )
    }
    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.resetSaved()
            snackbar.showToast(PharmToast.Success(s.commonSaved))
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
