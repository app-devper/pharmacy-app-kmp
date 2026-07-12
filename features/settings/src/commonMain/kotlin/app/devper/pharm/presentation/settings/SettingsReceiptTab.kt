package app.devper.pharm.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Icon
import app.devper.pharm.common.print.ReceiptLine
import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmIcons
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
    PharmButton(
        label = s.settingsTestPrintCta,
        onClick = {
            editor.onTestPrint(
                ReceiptTemplate(
                    storeName = f.storeName,
                    storeAddress = f.storeAddress,
                    storePhone = f.storePhone,
                    storeTaxId = f.storeTaxId,
                    billNo = "TEST-0001",
                    soldAt = "-",
                    customerName = "-",
                    items = listOf(
                        ReceiptLine(
                            name = s.settingsTestPrintSampleItem,
                            displayQty = 2,
                            displayUnit = s.commonUnitDefault,
                            unitPrice = 25.0,
                            lineTotal = 50.0,
                        ),
                    ),
                    subtotal = 50.0,
                    itemDiscountTotal = 0.0,
                    cartDiscount = 0.0,
                    total = 50.0,
                    received = 100.0,
                    change = 50.0,
                    pharmacistName = if (f.receiptShowPharmacist) f.pharmacistName else "",
                    footer = f.receiptFooter,
                ),
            )
        },
        variant = PharmButtonVariant.Outline,
        size = PharmButtonSize.Sm,
        leadingIcon = { Icon(PharmIcons.Print, contentDescription = null) },
    )
}
