package app.devper.pharm.presentation.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.users.i18n.localizeUserForm
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.components.SubPageBar
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
    var validationRequested by remember(state.mode) { mutableStateOf(false) }
    val firstNameFocus = remember { FocusRequester() }
    val lastNameFocus = remember { FocusRequester() }
    val usernameFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        SubPageBar(
            title = if (state.isEdit) s.usersFormEditTitle else s.usersFormAddTitle,
            onBack = callbacks.onBack,
            actions = {
                PharmSaveAction(
                    saving = state.saving,
                    canSubmit = state.canSubmit,
                    onSubmit = callbacks.onSubmit,
                    onInvalidSubmit = if (state.loading) null else {
                        {
                            validationRequested = true
                            when {
                                state.form.firstName.isBlank() -> firstNameFocus.requestFocus()
                                state.form.lastName.isBlank() -> lastNameFocus.requestFocus()
                                !state.isEdit && state.form.username.isBlank() -> usernameFocus.requestFocus()
                                !state.isEdit && state.form.password.length < 8 -> passwordFocus.requestFocus()
                            }
                        }
                    },
                )
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
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
                    FormFields(
                        state = state,
                        callbacks = callbacks,
                        showValidation = validationRequested,
                        firstNameFocus = firstNameFocus,
                        lastNameFocus = lastNameFocus,
                        usernameFocus = usernameFocus,
                        passwordFocus = passwordFocus,
                    )
                }
            }
        }
    }
    ErrorBottomSheet(message = state.errorState?.localizeUserForm(s), onDismiss = callbacks.onDismissError)
}

@Composable
private fun FormFields(
    state: UserFormUiState,
    callbacks: UserFormCallbacks,
    showValidation: Boolean,
    firstNameFocus: FocusRequester,
    lastNameFocus: FocusRequester,
    usernameFocus: FocusRequester,
    passwordFocus: FocusRequester,
) {
    val s = pharmStrings
    val isEdit = state.isEdit
    val firstNameError = showValidation && state.form.firstName.isBlank()
    val lastNameError = showValidation && state.form.lastName.isBlank()
    FormField(
        label = s.profileFirstName,
        required = true,
        error = if (firstNameError) s.validationRequired(s.profileFirstName) else null,
    ) {
        PharmTextField(
            value = state.form.firstName,
            onValueChange = callbacks.onFirstName,
            isError = firstNameError,
            modifier = Modifier.focusRequester(firstNameFocus),
        )
    }
    FormField(
        label = s.profileLastName,
        required = true,
        error = if (lastNameError) s.validationRequired(s.profileLastName) else null,
    ) {
        PharmTextField(
            value = state.form.lastName,
            onValueChange = callbacks.onLastName,
            isError = lastNameError,
            modifier = Modifier.focusRequester(lastNameFocus),
        )
    }
    val usernameError = showValidation && !isEdit && state.form.username.isBlank()
    FormField(
        label = s.usersFormUsername,
        required = !isEdit,
        hint = if (isEdit) s.usersCannotEdit else null,
        error = if (usernameError) s.validationRequired(s.usersFormUsername) else null,
    ) {
        PharmTextField(
            value = state.form.username,
            onValueChange = callbacks.onUsername,
            enabled = !isEdit,
            readOnly = isEdit,
            isError = usernameError,
            modifier = Modifier.focusRequester(usernameFocus),
        )
    }
    if (!isEdit) {
        val passwordMissing = showValidation && state.form.password.isBlank()
        val passwordTooShort = state.form.password.isNotBlank() && state.form.password.length < 8
        val passwordError = when {
            passwordMissing -> s.validationRequired(s.usersFormPasswordCreate)
            passwordTooShort -> s.usersFormPasswordHint
            else -> null
        }
        FormField(
            label = s.usersFormPasswordCreate,
            required = true,
            error = passwordError,
        ) {
            PharmTextField(
                value = state.form.password,
                onValueChange = callbacks.onPassword,
                visualTransformation = PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password,
                isError = passwordError != null,
                modifier = Modifier.focusRequester(passwordFocus),
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
