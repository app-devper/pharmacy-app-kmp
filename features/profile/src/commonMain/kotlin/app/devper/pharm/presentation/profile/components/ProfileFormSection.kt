package app.devper.pharm.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormField(label = strings.profileFirstName, required = true) {
            PharmTextField(value = state.form.firstName, onValueChange = callbacks.onFirstName)
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
                onClick = callbacks.onSubmit,
                enabled = state.canSubmit,
                variant = PharmButtonVariant.Primary,
            )
            if (state.saved) {
                Text(
                    text = strings.profileSavedInline,
                    style = PharmText.body.copy(color = t.colors.successFg),
                    modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically),
                )
            }
        }
    }
}
