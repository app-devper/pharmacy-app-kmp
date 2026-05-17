package app.devper.pharm.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmToggleSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsKyTab(state: SettingsEditorUiState, editor: SettingsEditorCallbacks) {
    val f = state.form
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text("ข้ามการบันทึก ขย. อัตโนมัติ", style = MaterialTheme.typography.bodyMedium)
            Text(
                "เมื่อเปิด ผู้ขายจะข้าม KyCaptureSheet ไปออกบิลทันที",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        PharmToggleSwitch(checked = f.kySkipAuto, onCheckedChange = editor.onKySkipAuto)
    }
    SettingsLabeledField(label = "ที่อยู่ผู้ซื้อเริ่มต้น (ขย.10)") {
        OutlinedTextField(
            value = f.kyDefaultBuyerAddress,
            onValueChange = editor.onKyDefaultBuyerAddress,
            placeholder = { Text("ใช้เป็นค่าเริ่มต้นเมื่อเปิด KyCaptureSheet") },
            singleLine = false,
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            ),
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp, max = 120.dp),
        )
    }
}
