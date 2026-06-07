package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmHelpHint
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular
import app.devper.pharm.ui.i18n.pharmStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KyCaptureSheet(
    required: KyRequired,

    initial: KyCaptureFields,
    submitting: Boolean,
    onConfirm: (KyCaptureFields) -> Unit,
    onSkip: () -> Unit,
    onDismiss: () -> Unit,
) {
    val t = pharmTokens
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var fields by remember(initial) { mutableStateOf(initial) }

    val canSubmit = !submitting && validate(required, fields)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = t.colors.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = pharmStrings.sellSaveKyBeforeBill,
                    style = PharmText.h1.copy(color = t.colors.fg1),
                    modifier = Modifier.weight(1f),
                )
                PharmHelpHint(
                    text = pharmStrings.sellControlledNote,
                )
            }
            Text(
                text = "ระบบจะออกบิลแล้วบันทึก ขย. ตามรายการต่อไปนี้",
                style = PharmText.body.copy(color = t.colors.fg2),
            )

            Box(Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

            if (required.needsKy11) {
                Ky11Section(
                    lines = required.ky11,
                    fields = fields,
                    onChange = { fields = it },
                    enabled = !submitting,
                )
            }
            if (required.needsKy10) {
                if (required.needsKy11) Spacer(Modifier.height(4.dp))
                Ky10Section(
                    lines = required.ky10,
                    fields = fields,
                    onChange = { fields = it },
                    enabled = !submitting,
                )
            }
            if (required.needsKy12) {
                if (required.needsKy10 || required.needsKy11) Spacer(Modifier.height(4.dp))
                Ky12Section(
                    lines = required.ky12,
                    fields = fields,
                    onChange = { fields = it },
                    enabled = !submitting,
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                PharmButton(
                    label = "ข้ามบันทึก ขย.",
                    onClick = onSkip,
                    enabled = !submitting,
                    variant = PharmButtonVariant.Ghost,
                )
                PharmButton(
                    label = pharmStrings.commonCancel,
                    onClick = onDismiss,
                    enabled = !submitting,
                    variant = PharmButtonVariant.Ghost,
                )
                PharmButton(
                    label = pharmStrings.sellCheckoutSave,
                    onClick = { onConfirm(fields) },
                    enabled = canSubmit,
                    loading = submitting,
                    variant = PharmButtonVariant.Primary,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    label: String,
    sublabel: String,
    bgColor: Color,
    fgColor: Color,
) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.sm)
            .background(bgColor, t.shapes.sm)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = PharmText.h2.copy(color = fgColor),
        )
        Text(
            text = sublabel,
            style = PharmText.bodySm.tabular().copy(color = t.colors.fg2),
        )
    }
}

@Composable
private fun Ky11Section(
    lines: List<CartLine>,
    fields: KyCaptureFields,
    onChange: (KyCaptureFields) -> Unit,
    enabled: Boolean,
) {
    val t = pharmTokens
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(
            label = "ขย.11 — ยาอันตราย",
            sublabel = lines.joinToString(", ") { it.drug.name },
            bgColor = t.colors.ky11Bg,
            fgColor = t.colors.ky11Fg,
        )
        KyField(
            label = pharmStrings.sellBuyerName,
            required = true,
            value = fields.ky11BuyerName,
            onValueChange = { onChange(fields.copy(ky11BuyerName = it)) },
            enabled = enabled,
        )
        KyField(
            label = pharmStrings.sellPurpose,
            required = true,
            value = fields.ky11Purpose,
            onValueChange = { onChange(fields.copy(ky11Purpose = it)) },
            enabled = enabled,
        )
        KyField(
            label = pharmStrings.sellPharmacist,
            required = true,
            value = fields.ky11Pharmacist,
            onValueChange = { onChange(fields.copy(ky11Pharmacist = it)) },
            enabled = enabled,
        )
    }
}

@Composable
private fun Ky10Section(
    lines: List<CartLine>,
    fields: KyCaptureFields,
    onChange: (KyCaptureFields) -> Unit,
    enabled: Boolean,
) {
    val t = pharmTokens
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(
            label = "ขย.10 — ยาควบคุมพิเศษ",
            sublabel = lines.joinToString(", ") { it.drug.name },
            bgColor = t.colors.ky10Bg,
            fgColor = t.colors.ky10Fg,
        )
        KyField(
            label = pharmStrings.sellBuyerName,
            required = true,
            value = fields.ky10BuyerName,
            onValueChange = { onChange(fields.copy(ky10BuyerName = it)) },
            enabled = enabled,
        )
        KyField(
            label = pharmStrings.sellBuyerAddress,
            required = true,
            value = fields.ky10BuyerAddress,
            onValueChange = { onChange(fields.copy(ky10BuyerAddress = it)) },
            enabled = enabled,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KyField(
                label = pharmStrings.sellPrescriptionNo,
                value = fields.ky10RxNo,
                onValueChange = { onChange(fields.copy(ky10RxNo = it)) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
            KyField(
                label = pharmStrings.sellPrescriber,
                value = fields.ky10Doctor,
                onValueChange = { onChange(fields.copy(ky10Doctor = it)) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
        }
        KyField(
            label = pharmStrings.sellRemaining,
            value = fields.ky10Balance.toString(),
            onValueChange = { v ->
                onChange(fields.copy(ky10Balance = v.toIntOrNull() ?: 0))
            },
            enabled = enabled,
            keyboardType = KeyboardType.Number,
        )
    }
}

@Composable
private fun Ky12Section(
    lines: List<CartLine>,
    fields: KyCaptureFields,
    onChange: (KyCaptureFields) -> Unit,
    enabled: Boolean,
) {
    val t = pharmTokens
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(
            label = "ขย.12 — ใบสั่งแพทย์",
            sublabel = lines.joinToString(", ") { it.drug.name },
            bgColor = t.colors.ky12Bg,
            fgColor = t.colors.ky12Fg,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KyField(
                label = pharmStrings.sellPrescriptionNo,
                required = true,
                value = fields.ky12RxNo,
                onValueChange = { onChange(fields.copy(ky12RxNo = it)) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
            KyField(
                label = pharmStrings.sellPatient,
                required = true,
                value = fields.ky12PatientName,
                onValueChange = { onChange(fields.copy(ky12PatientName = it)) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KyField(
                label = pharmStrings.kyDoctorPrescriber,
                value = fields.ky12Doctor,
                onValueChange = { onChange(fields.copy(ky12Doctor = it)) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
            KyField(
                label = pharmStrings.sellHospital,
                value = fields.ky12Hospital,
                onValueChange = { onChange(fields.copy(ky12Hospital = it)) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
        }
        KyField(
            label = pharmStrings.commonStatus,
            value = fields.ky12Status,
            onValueChange = { onChange(fields.copy(ky12Status = it)) },
            enabled = enabled,
        )
    }
}

@Composable
private fun KyField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    FormField(label = label, required = required, modifier = modifier) {
        PharmTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            keyboardType = keyboardType,
        )
    }
}

private fun validate(required: KyRequired, fields: KyCaptureFields): Boolean {
    if (required.needsKy11 && (
            fields.ky11BuyerName.isBlank() ||
                fields.ky11Purpose.isBlank() ||
                fields.ky11Pharmacist.isBlank()
            )
    ) return false
    if (required.needsKy10 && (
            fields.ky10BuyerName.isBlank() ||
                fields.ky10BuyerAddress.isBlank()
            )
    ) return false
    if (required.needsKy12 && (
            fields.ky12RxNo.isBlank() ||
                fields.ky12PatientName.isBlank()
            )
    ) return false
    return true
}
