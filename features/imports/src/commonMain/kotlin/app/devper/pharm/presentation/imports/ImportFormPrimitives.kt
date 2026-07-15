package app.devper.pharm.presentation.imports

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmTextField

@Composable
internal fun ImportLabeledField(
    label: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    error: String? = null,
    content: @Composable () -> Unit,
) {
    FormField(
        label = label,
        modifier = modifier,
        required = required,
        error = error,
        content = content,
    )
}

@Composable
internal fun ImportFormField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
) {
    PharmTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        enabled = enabled,
        keyboardType = keyboardType,
        isError = isError,
        modifier = modifier,
    )
}
