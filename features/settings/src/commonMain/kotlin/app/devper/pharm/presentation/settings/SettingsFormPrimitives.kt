package app.devper.pharm.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardType
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmTextField

@Composable
internal fun SettingsLabeledField(
    label: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    hint: String? = null,
    error: String? = null,
    content: @Composable () -> Unit,
) {
    FormField(
        label = label,
        modifier = modifier,
        required = required,
        hint = hint,
        error = error,
    ) {
        content()
    }
}

@Composable
internal fun SettingsFormField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    focusRequester: FocusRequester? = null,
) {
    PharmTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        keyboardType = keyboardType,
        isError = isError,
        focusRequester = focusRequester,
    )
}
