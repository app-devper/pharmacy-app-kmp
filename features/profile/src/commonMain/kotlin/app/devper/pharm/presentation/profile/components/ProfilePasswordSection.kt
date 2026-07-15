package app.devper.pharm.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.profile.ProfileCallbacks
import app.devper.pharm.presentation.profile.ProfileUiState
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun ProfilePasswordSection(
    state: ProfileUiState,
    callbacks: ProfileCallbacks,
) {
    val strings = pharmStrings
    var validationRequested by rememberSaveable(state.showPasswordPanel) { mutableStateOf(false) }
    val oldPasswordFocus = remember(state.showPasswordPanel) { FocusRequester() }
    val newPasswordFocus = remember(state.showPasswordPanel) { FocusRequester() }
    val confirmPasswordFocus = remember(state.showPasswordPanel) { FocusRequester() }
    val oldPasswordError = validationRequested && state.password.oldPassword.isBlank()
    val newPasswordError = validationRequested && !state.password.newPasswordValid
    val confirmPasswordError = validationRequested && !state.password.matches
    val submitPassword: () -> Unit = {
        if (state.password.canSubmit) {
            callbacks.onSubmitPasswordChange()
        } else {
            validationRequested = true
            when {
                state.password.oldPassword.isBlank() -> oldPasswordFocus.requestFocus()
                !state.password.newPasswordValid -> newPasswordFocus.requestFocus()
                else -> confirmPasswordFocus.requestFocus()
            }
            Unit
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (!state.showPasswordPanel) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.profilePasswordIntro,
                    style = PharmText.body.copy(color = pharmTokens.colors.fg2),
                    modifier = Modifier.weight(1f),
                )
                PharmButton(
                    label = strings.profilePasswordChange,
                    onClick = callbacks.onOpenPasswordPanel,
                    variant = PharmButtonVariant.Secondary,
                )
            }
        } else {
            FormField(
                label = strings.profilePasswordOld,
                required = true,
                error = if (oldPasswordError) strings.validationRequired(strings.profilePasswordOld) else null,
            ) {
                PharmTextField(
                    value = state.password.oldPassword,
                    onValueChange = callbacks.onOldPassword,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardType = KeyboardType.Password,
                    isError = oldPasswordError,
                    focusRequester = oldPasswordFocus,
                )
            }
            FormField(
                label = strings.profilePasswordNew,
                required = true,
                error = if (newPasswordError) strings.profileSectionPasswordSubtitle else null,
            ) {
                PharmTextField(
                    value = state.password.newPassword,
                    onValueChange = callbacks.onNewPassword,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardType = KeyboardType.Password,
                    isError = newPasswordError,
                    focusRequester = newPasswordFocus,
                )
            }
            FormField(
                label = strings.profilePasswordConfirm,
                required = true,
                error = if (confirmPasswordError) {
                    if (state.password.confirmPassword.isBlank()) {
                        strings.validationRequired(strings.profilePasswordConfirm)
                    } else {
                        strings.profilePasswordMismatch
                    }
                } else {
                    null
                },
            ) {
                PharmTextField(
                    value = state.password.confirmPassword,
                    onValueChange = callbacks.onConfirmPassword,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onImeAction = submitPassword,
                    isError = confirmPasswordError,
                    focusRequester = confirmPasswordFocus,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PharmButton(
                    label = if (state.passwordSaving) strings.profilePasswordChanging else strings.commonSave,
                    onClick = submitPassword,
                    enabled = !state.passwordSaving,
                    variant = PharmButtonVariant.Primary,
                )
                PharmButton(
                    label = strings.commonCancel,
                    onClick = callbacks.onClosePasswordPanel,
                    variant = PharmButtonVariant.Ghost,
                )
            }
        }
        if (state.passwordSaved) {
            Text(
                text = strings.profilePasswordChanged,
                style = PharmText.body.copy(color = pharmTokens.colors.successFg),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
    }
}
