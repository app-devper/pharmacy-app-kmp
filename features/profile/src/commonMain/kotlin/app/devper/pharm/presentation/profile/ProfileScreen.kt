package app.devper.pharm.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.components.RegisterUnsavedChanges
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RegisterUnsavedChanges(state.hasUnsavedChanges)

    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.resetSaved()
        }
    }

    ProfileContent(
        state = state,
        callbacks = ProfileCallbacks(
            onFirstName = viewModel::onFirstName,
            onLastName = viewModel::onLastName,
            onPhone = viewModel::onPhone,
            onEmail = viewModel::onEmail,
            onSubmit = viewModel::submit,
            onDismissError = viewModel::dismissError,
            onOpenPasswordPanel = viewModel::openPasswordPanel,
            onClosePasswordPanel = viewModel::closePasswordPanel,
            onOldPassword = viewModel::onOldPassword,
            onNewPassword = viewModel::onNewPassword,
            onConfirmPassword = viewModel::onConfirmPassword,
            onSubmitPasswordChange = viewModel::submitPasswordChange,
            onDismissPasswordError = viewModel::dismissPasswordError,
            onThemeChange = viewModel::onThemeChange,
            onFontSizeChange = viewModel::onFontSizeChange,
            onDensityChange = viewModel::onDensityChange,
            onLocaleChange = viewModel::onLocaleChange,
        ),
    )
}
