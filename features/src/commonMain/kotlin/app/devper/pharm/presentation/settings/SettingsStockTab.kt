package app.devper.pharm.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType

@Composable
internal fun SettingsStockTab(state: SettingsEditorUiState, editor: SettingsEditorCallbacks) {
    val f = state.form
    SettingsLabeledField(label = "เกณฑ์สต็อกขั้นต่ำ (ของยาที่ไม่ระบุ min_stock)") {
        SettingsFormField(
            value = f.stockLowThreshold,
            onValueChange = editor.onStockLowThreshold,
            keyboardType = KeyboardType.Number,
            placeholder = "0 = ไม่แจ้งเตือน",
        )
    }
    SettingsLabeledField(label = "ช่วงเวลาวิเคราะห์ Reorder (วัน)") {
        SettingsFormField(
            value = f.stockReorderDays,
            onValueChange = editor.onStockReorderDays,
            keyboardType = KeyboardType.Number,
            placeholder = "30",
        )
    }
    SettingsLabeledField(label = "Lookahead เป้า cover (วัน)") {
        SettingsFormField(
            value = f.stockReorderLookahead,
            onValueChange = editor.onStockReorderLookahead,
            keyboardType = KeyboardType.Number,
            placeholder = "14",
        )
    }
    SettingsLabeledField(label = "ช่วงเตือนใกล้หมดอายุ (วัน)") {
        SettingsFormField(
            value = f.stockExpiringDays,
            onValueChange = editor.onStockExpiringDays,
            keyboardType = KeyboardType.Number,
            placeholder = "60",
        )
    }
}
