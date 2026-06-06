package app.devper.pharm.presentation.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmSaveAction
import app.devper.pharm.ui.designsystem.PharmSubPage
import app.devper.pharm.ui.designsystem.PharmTextField
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
    val loadingEmpty = state.loading && state.form.firstName.isBlank()
    PharmSubPage(
        title = state.titleLabel,
        onBack = callbacks.onBack,
        scrollable = !loadingEmpty,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        actions = {
            PharmSaveAction(
                saving = state.saving,
                canSubmit = state.canSubmit,
                onSubmit = callbacks.onSubmit,
            )
        },
    ) {
        if (loadingEmpty) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                contentAlignment = Alignment.Center,
            ) {
                PharmCircularProgress(color = t.colors.accent)
            }
        } else {
            Column(
                modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PharmFormCard(title = "ข้อมูลผู้ใช้") {
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
    val isEdit = state.isEdit
    FormField(label = "ชื่อ", required = true) {
        PharmTextField(value = state.form.firstName, onValueChange = callbacks.onFirstName)
    }
    FormField(label = "นามสกุล", required = true) {
        PharmTextField(value = state.form.lastName, onValueChange = callbacks.onLastName)
    }
    FormField(
        label = "ชื่อผู้ใช้",
        required = !isEdit,
        hint = if (isEdit) "ไม่สามารถแก้ไขได้" else null,
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
            label = "รหัสผ่าน (≥8 ตัว)",
            required = true,
            error = if (pwdError) "รหัสผ่านต้องไม่น้อยกว่า 8 ตัวอักษร" else null,
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
    FormField(label = "เบอร์โทร") {
        PharmTextField(
            value = state.form.phone,
            onValueChange = callbacks.onPhone,
            keyboardType = KeyboardType.Phone,
        )
    }
    FormField(label = "อีเมล") {
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
