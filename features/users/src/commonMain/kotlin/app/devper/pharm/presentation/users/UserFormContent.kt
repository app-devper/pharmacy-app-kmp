package app.devper.pharm.presentation.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSaveAction
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@Composable
fun UserFormContent(
    state: UserFormUiState,
    callbacks: UserFormCallbacks,
) {
    val t = pharmTokens
    val s = pharmStrings
    val loadingEmpty = state.loading && state.form.firstName.isBlank()
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = if (state.isEdit) s.usersFormEditTitle else s.usersFormAddTitle,
            onBack = callbacks.onBack,
            actions = {
                PharmSaveAction(
                    saving = state.saving,
                    canSubmit = state.canSubmit,
                    onSubmit = callbacks.onSubmit,
                )
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (loadingEmpty) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    PharmCircularProgress(color = t.colors.accent)
                }
            } else {
                PharmFormCard(title = s.usersFormInfoSection) {
                    FormFields(state = state, callbacks = callbacks)
                }
            }
        }
    }
    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun FormFields(
    state: UserFormUiState,
    callbacks: UserFormCallbacks,
) {
    val s = pharmStrings
    val isEdit = state.isEdit
    FormField(label = s.profileFirstName, required = true) {
        PharmTextField(value = state.form.firstName, onValueChange = callbacks.onFirstName)
    }
    FormField(label = s.profileLastName, required = true) {
        PharmTextField(value = state.form.lastName, onValueChange = callbacks.onLastName)
    }
    FormField(
        label = s.usersFormUsername,
        required = !isEdit,
        hint = if (isEdit) s.usersCannotEdit else null,
    ) {
        PharmTextField(
            value = state.form.username,
            onValueChange = callbacks.onUsername,
            enabled = !isEdit,
            readOnly = isEdit,
        )
    }
    if (!isEdit) {
        val pwdError = state.form.password.isNotBlank() && state.form.password.length < 8
        FormField(
            label = s.usersFormPasswordCreate,
            required = true,
            error = if (pwdError) s.usersFormPasswordHint else null,
        ) {
            PharmTextField(
                value = state.form.password,
                onValueChange = callbacks.onPassword,
                visualTransformation = PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password,
                isError = pwdError,
            )
        }
    }
    FormField(label = s.commonPhone) {
        PharmTextField(
            value = state.form.phone,
            onValueChange = callbacks.onPhone,
            keyboardType = KeyboardType.Phone,
        )
    }
    FormField(label = s.profileEmail) {
        PharmTextField(
            value = state.form.email,
            onValueChange = callbacks.onEmail,
            keyboardType = KeyboardType.Email,
        )
    }
}

@Preview
@Composable
private fun UserFormContent_Add_Preview() {
    PharmacyTheme {
        UserFormContent(state = UserFormUiState(), callbacks = UserFormCallbacks.Preview)
    }
}

@Preview
@Composable
private fun UserFormContent_Edit_Preview() {
    PharmacyTheme {
        UserFormContent(
            state = UserFormUiState(
                mode = UserFormMode.Edit("u-1"),
                form = UserFormFields(
                    firstName = "สมชาย", lastName = "ใจดี",
                    username = "somchai", phone = "0812345678", email = "somchai@example.com",
                ),
            ),
            callbacks = UserFormCallbacks.Preview,
        )
    }
}

@Preview
@Composable
private fun UserFormContent_Saving_Preview() {
    PharmacyTheme {
        UserFormContent(
            state = UserFormUiState(
                form = UserFormFields(firstName = "สมชาย", lastName = "ใจดี", username = "somchai", password = "password1"),
                saving = true,
            ),
            callbacks = UserFormCallbacks.Preview,
        )
    }
}
