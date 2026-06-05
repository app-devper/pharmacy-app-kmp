package app.devper.pharm.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.designsystem.PharmToggleSwitch
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun SettingsKyTab(state: SettingsEditorUiState, editor: SettingsEditorCallbacks) {
    val t = pharmTokens
    val f = state.form
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text("ข้ามการบันทึก ขย. อัตโนมัติ", style = PharmText.body.copy(color = t.colors.fg1))
            Text(
                "เมื่อเปิด ผู้ขายจะข้าม KyCaptureSheet ไปออกบิลทันที",
                style = PharmText.micro.copy(color = t.colors.fg3),
            )
        }
        PharmToggleSwitch(checked = f.kySkipAuto, onCheckedChange = editor.onKySkipAuto)
    }
    SettingsLabeledField(label = "ที่อยู่ผู้ซื้อเริ่มต้น (ขย.10)") {
        Box(modifier = Modifier.heightIn(min = 56.dp, max = 120.dp)) {
            PharmTextField(
                value = f.kyDefaultBuyerAddress,
                onValueChange = editor.onKyDefaultBuyerAddress,
                placeholder = "ใช้เป็นค่าเริ่มต้นเมื่อเปิด KyCaptureSheet",
                singleLine = false,
            )
        }
    }
}
