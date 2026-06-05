package app.devper.pharm.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmTextField

@Composable
internal fun SettingsLabeledField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    FormField(label = label, modifier = modifier) {
        content()
    }
}

@Composable
internal fun SettingsFormField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    PharmTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        keyboardType = keyboardType,
    )
}
