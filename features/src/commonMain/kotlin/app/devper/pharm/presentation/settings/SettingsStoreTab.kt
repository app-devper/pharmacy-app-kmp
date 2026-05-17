package app.devper.pharm.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
internal fun SettingsStoreTab(state: SettingsEditorUiState, editor: SettingsEditorCallbacks) {
    val f = state.form
    SettingsLabeledField(label = "ชื่อร้าน *") {
        SettingsFormField(value = f.storeName, onValueChange = editor.onStoreName, placeholder = "เช่น ร้านยาดี")
    }
    SettingsLabeledField(label = "ที่อยู่") {
        SettingsFormField(value = f.storeAddress, onValueChange = editor.onStoreAddress)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SettingsLabeledField(label = "เบอร์โทร", modifier = Modifier.weight(1f)) {
            SettingsFormField(value = f.storePhone, onValueChange = editor.onStorePhone, keyboardType = KeyboardType.Phone)
        }
        SettingsLabeledField(label = "เลขผู้เสียภาษี", modifier = Modifier.weight(1f)) {
            SettingsFormField(value = f.storeTaxId, onValueChange = editor.onStoreTaxId)
        }
    }
    SettingsLabeledField(label = "เขตเวลา (IANA)") {
        SettingsFormField(value = f.timezone, onValueChange = editor.onTimezone, placeholder = "Asia/Bangkok")
    }
}
