package app.devper.pharm.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
internal fun SettingsStockTab(state: SettingsEditorUiState, editor: SettingsEditorCallbacks) {
    val f = state.form
    val s = pharmStrings
    SettingsLabeledField(label = s.settingsStockLowThresholdLabel) {
        SettingsFormField(
            value = f.stockLowThreshold,
            onValueChange = editor.onStockLowThreshold,
            keyboardType = KeyboardType.Number,
            placeholder = s.settingsStockLowThresholdPlaceholder,
        )
    }
    SettingsLabeledField(label = s.settingsStockReorderDays) {
        SettingsFormField(
            value = f.stockReorderDays,
            onValueChange = editor.onStockReorderDays,
            keyboardType = KeyboardType.Number,
            placeholder = "30",
        )
    }
    SettingsLabeledField(label = s.settingsStockReorderLookahead) {
        SettingsFormField(
            value = f.stockReorderLookahead,
            onValueChange = editor.onStockReorderLookahead,
            keyboardType = KeyboardType.Number,
            placeholder = "14",
        )
    }
    SettingsLabeledField(label = s.settingsStockExpiringDays) {
        SettingsFormField(
            value = f.stockExpiringDays,
            onValueChange = editor.onStockExpiringDays,
            keyboardType = KeyboardType.Number,
            placeholder = "60",
        )
    }
}
