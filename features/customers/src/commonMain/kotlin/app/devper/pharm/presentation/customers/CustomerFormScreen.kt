package app.devper.pharm.presentation.customers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.presentation.customers.form.CustomerFormCallbacks
import app.devper.pharm.presentation.customers.form.CustomerFormContent
import app.devper.pharm.ui.components.RegisterUnsavedChanges
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CustomerFormScreen(
    customerId: String?,
    onBack: () -> Unit,
    viewModel: CustomerFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RegisterUnsavedChanges(state.hasUnsavedChanges)

    LaunchedEffect(customerId) {
        viewModel.init(
            if (customerId.isNullOrBlank()) CustomerFormMode.Add
            else CustomerFormMode.Edit(customerId),
        )
    }
    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.resetSaved()
            onBack()
        }
    }

    CustomerFormContent(
        state = state,
        callbacks = CustomerFormCallbacks(
            onSubmit = viewModel::submit,
            onBack = onBack,
            onDismissError = viewModel::dismissError,
            onName = viewModel::onName,
            onPhone = viewModel::onPhone,
            onAllergyNote = viewModel::onAllergyNote,
            onPriceTier = viewModel::onPriceTier,
        ),
    )
}
