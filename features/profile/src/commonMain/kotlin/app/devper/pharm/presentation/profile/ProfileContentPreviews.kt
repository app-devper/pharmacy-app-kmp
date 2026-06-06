package app.devper.pharm.presentation.profile

import androidx.compose.runtime.Composable
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.ui.theme.PharmacyTheme
import androidx.compose.ui.tooling.preview.Preview

private val PreviewUser = UmUser(
    id = "u1",
    firstName = "ปรียา",
    lastName = "ใจดี",
    username = "pharm01",
    clientId = "001",
    role = Role.ADMIN,
    status = UmStatus.ACTIVE,
    phone = "081-234-5678",
    email = "preeya@healthypharm.co.th",
    createdDate = "",
    updatedDate = "",
)

private val PreviewForm = ProfileFormFields(
    firstName = PreviewUser.firstName,
    lastName = PreviewUser.lastName,
    phone = PreviewUser.phone,
    email = PreviewUser.email,
)

@Preview
@Composable
private fun ProfileContent_Loaded_Preview() {
    PharmacyTheme {
        ProfileContent(
            state = ProfileUiState(user = PreviewUser, form = PreviewForm),
            callbacks = ProfileCallbacks.Preview,
        )
    }
}

@Preview
@Composable
private fun ProfileContent_PasswordPanel_Preview() {
    PharmacyTheme {
        ProfileContent(
            state = ProfileUiState(
                user = PreviewUser,
                form = PreviewForm,
                showPasswordPanel = true,
                password = PasswordFormFields(oldPassword = "secret"),
            ),
            callbacks = ProfileCallbacks.Preview,
        )
    }
}

@Preview
@Composable
private fun ProfileContent_Loading_Preview() {
    PharmacyTheme {
        ProfileContent(
            state = ProfileUiState(loading = true),
            callbacks = ProfileCallbacks.Preview,
        )
    }
}

@Preview
@Composable
private fun ProfileContent_DisplayPrefs_Preview() {
    PharmacyTheme {
        ProfileContent(
            state = ProfileUiState(
                user = PreviewUser,
                form = PreviewForm,
                theme = "dark",
                fontSize = "lg",
            ),
            callbacks = ProfileCallbacks.Preview,
        )
    }
}
