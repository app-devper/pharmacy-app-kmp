package app.devper.pharm.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.KeyboardType
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
internal fun ProfileFormSection(
    state: ProfileUiState,
    callbacks: ProfileCallbacks,
) {
    val t = pharmTokens
    val strings = pharmStrings
    var validationRequested by rememberSaveable { mutableStateOf(false) }
    val firstNameFocus = remember { FocusRequester() }
    val firstNameError = validationRequested && state.form.firstName.isBlank()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormField(
            label = strings.profileFirstName,
            required = true,
            error = if (firstNameError) strings.validationRequired(strings.profileFirstName) else null,
        ) {
            PharmTextField(
                value = state.form.firstName,
                onValueChange = callbacks.onFirstName,
                isError = firstNameError,
                focusRequester = firstNameFocus,
            )
        }
        FormField(label = strings.profileLastName) {
            PharmTextField(value = state.form.lastName, onValueChange = callbacks.onLastName)
        }
        FormField(label = strings.commonPhone) {
            PharmTextField(
                value = state.form.phone,
                onValueChange = callbacks.onPhone,
                keyboardType = KeyboardType.Phone,
            )
        }
        FormField(label = strings.profileEmail) {
            PharmTextField(
                value = state.form.email,
                onValueChange = callbacks.onEmail,
                keyboardType = KeyboardType.Email,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PharmButton(
                label = if (state.saving) strings.profileSaving else strings.commonSave,
                onClick = {
                    if (state.canSubmit) {
                        callbacks.onSubmit()
                    } else {
                        validationRequested = true
                        firstNameFocus.requestFocus()
                    }
                },
                enabled = state.canAttemptSubmit,
                variant = PharmButtonVariant.Primary,
            )
            if (state.saved) {
                Text(
                    text = strings.profileSavedInline,
                    style = PharmText.body.copy(color = t.colors.successFg),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                )
            }
        }
    }
}
