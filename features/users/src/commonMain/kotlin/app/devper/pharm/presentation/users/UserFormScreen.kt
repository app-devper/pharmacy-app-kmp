package app.devper.pharm.presentation.users

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserFormScreen(
    userId: String?,
    onBack: () -> Unit,
    viewModel: UserFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.init(
            if (userId.isNullOrBlank()) UserFormMode.Add
            else UserFormMode.Edit(userId),
        )
    }
    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.resetSaved()
            onBack()
        }
    }

    UserFormContent(
        state = state,
        callbacks = UserFormCallbacks(
            onFirstName = viewModel::onFirstName,
            onLastName = viewModel::onLastName,
            onUsername = viewModel::onUsername,
            onPassword = viewModel::onPassword,
            onPhone = viewModel::onPhone,
            onEmail = viewModel::onEmail,
            onSubmit = viewModel::submit,
            onBack = onBack,
            onDismissError = viewModel::dismissError,
        ),
    )
}
