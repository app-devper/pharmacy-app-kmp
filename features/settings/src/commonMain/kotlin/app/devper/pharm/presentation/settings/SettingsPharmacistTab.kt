package app.devper.pharm.presentation.settings

import androidx.compose.runtime.Composable

@Composable
internal fun SettingsPharmacistTab(state: SettingsEditorUiState, editor: SettingsEditorCallbacks) {
    val f = state.form
    SettingsLabeledField(label = "ชื่อเภสัชกร") {
        SettingsFormField(value = f.pharmacistName, onValueChange = editor.onPharmacistName)
    }
    SettingsLabeledField(label = "เลขที่ใบประกอบวิชาชีพ") {
        SettingsFormField(value = f.pharmacistLicenseNo, onValueChange = editor.onPharmacistLicenseNo)
    }
}
