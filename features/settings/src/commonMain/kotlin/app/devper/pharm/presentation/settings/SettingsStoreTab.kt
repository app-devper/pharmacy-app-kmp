package app.devper.pharm.presentation.settings

import app.devper.pharm.ui.components.PharmBreakpoint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
internal fun SettingsStoreTab(
    state: SettingsEditorUiState,
    editor: SettingsEditorCallbacks,
    showValidation: Boolean,
    focus: SettingsFocusRequesters,
) {
    val f = state.form
    val s = pharmStrings
    val storeNameError = if (showValidation && !f.storeNameValid) {
        s.validationRequired(s.settingsStoreNameLabel)
    } else null
    SettingsLabeledField(
        label = s.settingsStoreNameLabel,
        required = true,
        error = storeNameError,
    ) {
        SettingsFormField(
            value = f.storeName,
            onValueChange = editor.onStoreName,
            placeholder = s.settingsStoreNamePlaceholder,
            isError = storeNameError != null,
            focusRequester = focus.storeName,
        )
    }
    SettingsLabeledField(label = s.settingsStoreAddress) {
        SettingsFormField(value = f.storeAddress, onValueChange = editor.onStoreAddress)
    }
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val twoCol = maxWidth >= PharmBreakpoint.FormTwoCol
        if (twoCol) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingsLabeledField(label = s.commonPhone, modifier = Modifier.weight(1f)) {
                    SettingsFormField(value = f.storePhone, onValueChange = editor.onStorePhone, keyboardType = KeyboardType.Phone)
                }
                SettingsLabeledField(label = s.settingsStoreTaxId, modifier = Modifier.weight(1f)) {
                    SettingsFormField(value = f.storeTaxId, onValueChange = editor.onStoreTaxId)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingsLabeledField(label = s.commonPhone) {
                    SettingsFormField(value = f.storePhone, onValueChange = editor.onStorePhone, keyboardType = KeyboardType.Phone)
                }
                SettingsLabeledField(label = s.settingsStoreTaxId) {
                    SettingsFormField(value = f.storeTaxId, onValueChange = editor.onStoreTaxId)
                }
            }
        }
    }
    val timezoneError = if (showValidation && !f.timezoneValid) s.settingsStoreTimezoneInvalid else null
    SettingsLabeledField(
        label = s.settingsStoreTimezone,
        error = timezoneError,
    ) {
        SettingsFormField(
            value = f.timezone,
            onValueChange = editor.onTimezone,
            placeholder = "Asia/Bangkok",
            isError = timezoneError != null,
            focusRequester = focus.timezone,
        )
    }
}
