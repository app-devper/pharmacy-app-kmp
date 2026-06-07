package app.devper.pharm.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.designsystem.PharmToggleSwitch
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun SettingsReceiptTab(state: SettingsEditorUiState, editor: SettingsEditorCallbacks) {
    val t = pharmTokens
    val s = pharmStrings
    val f = state.form
    SettingsLabeledField(label = s.settingsReceiptHeader) {
        SettingsFormField(
            value = f.receiptHeader,
            onValueChange = editor.onReceiptHeader,
            placeholder = s.settingsReceiptHeaderPlaceholder,
        )
    }
    SettingsLabeledField(label = s.settingsReceiptFooter) {
        SettingsFormField(
            value = f.receiptFooter,
            onValueChange = editor.onReceiptFooter,
            placeholder = s.settingsReceiptFooterPlaceholder,
        )
    }
    SettingsLabeledField(label = s.settingsReceiptPaperWidth) {
        PharmSingleSelectChips(
            chips = listOf(
                PharmFilterChip(id = "58", label = "58 mm"),
                PharmFilterChip(id = "80", label = "80 mm"),
            ),
            activeId = f.receiptPaperWidth,
            onSelect = editor.onReceiptPaperWidth,
            scrollable = false,
        )
    }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(s.settingsReceiptShowPharmacist, style = PharmText.body.copy(color = t.colors.fg1))
            Text(
                s.settingsReceiptFooterHint,
                style = PharmText.micro.copy(color = t.colors.fg3),
            )
        }
        PharmToggleSwitch(checked = f.receiptShowPharmacist, onCheckedChange = editor.onReceiptShowPharmacist)
    }
}
