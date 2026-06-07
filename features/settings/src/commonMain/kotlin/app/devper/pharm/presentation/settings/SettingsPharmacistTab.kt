package app.devper.pharm.presentation.settings

import androidx.compose.runtime.Composable
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
internal fun SettingsPharmacistTab(state: SettingsEditorUiState, editor: SettingsEditorCallbacks) {
    val f = state.form
    val s = pharmStrings
    SettingsLabeledField(label = s.settingsPharmacistName) {
        SettingsFormField(value = f.pharmacistName, onValueChange = editor.onPharmacistName)
    }
    SettingsLabeledField(label = s.settingsPharmacistLicenseNo) {
        SettingsFormField(value = f.pharmacistLicenseNo, onValueChange = editor.onPharmacistLicenseNo)
    }
}
