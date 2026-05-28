package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.ui.theme.tabular
import app.devper.pharm.ui.designsystem.PharmCircularProgress

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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var fields by remember(initial) { mutableStateOf(initial) }

    val canSubmit = !submitting && validate(required, fields)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "บันทึก ขย. ก่อนออกบิล",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "ระบบจะออกบิลแล้วบันทึก ขย. ตามรายการต่อไปนี้",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

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
                TextButton(onClick = onSkip, enabled = !submitting) {
                    Text("ข้ามบันทึก ขย.", style = MaterialTheme.typography.titleMedium)
                }
                TextButton(onClick = onDismiss, enabled = !submitting) {
                    Text("ยกเลิก", style = MaterialTheme.typography.titleMedium)
                }
                Button(
                    onClick = { onConfirm(fields) },
                    enabled = canSubmit,
                ) {
                    if (submitting) {
                        PharmCircularProgress(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.height(18.dp),
                        )
                    } else {
                        Text("บันทึกและออกบิล", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    label: String,
    sublabel: String,
    badgeColor: androidx.compose.ui.graphics.Color,
) {
    Surface(
        color = badgeColor.copy(alpha = 0.18f),
        shape = MaterialTheme.shapes.small,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = sublabel,
                style = MaterialTheme.typography.bodySmall.tabular(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Ky11Section(
    lines: List<CartLine>,
    fields: KyCaptureFields,
    onChange: (KyCaptureFields) -> Unit,
    enabled: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(
            label = "ขย.11 — ยาอันตราย",
            sublabel = lines.joinToString(", ") { it.drug.name },
            badgeColor = MaterialTheme.colorScheme.error,
        )
        KyField(
            label = "ชื่อผู้ซื้อ *",
            value = fields.ky11BuyerName,
            onValueChange = { onChange(fields.copy(ky11BuyerName = it)) },
            enabled = enabled,
        )
        KyField(
            label = "วัตถุประสงค์ *",
            value = fields.ky11Purpose,
            onValueChange = { onChange(fields.copy(ky11Purpose = it)) },
            enabled = enabled,
        )
        KyField(
            label = "เภสัชกรผู้จ่าย *",
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(
            label = "ขย.10 — ยาควบคุมพิเศษ",
            sublabel = lines.joinToString(", ") { it.drug.name },
            badgeColor = MaterialTheme.colorScheme.tertiary,
        )
        KyField(
            label = "ชื่อผู้ซื้อ *",
            value = fields.ky10BuyerName,
            onValueChange = { onChange(fields.copy(ky10BuyerName = it)) },
            enabled = enabled,
        )
        KyField(
            label = "ที่อยู่ผู้ซื้อ *",
            value = fields.ky10BuyerAddress,
            onValueChange = { onChange(fields.copy(ky10BuyerAddress = it)) },
            enabled = enabled,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KyField(
                label = "เลขใบสั่ง",
                value = fields.ky10RxNo,
                onValueChange = { onChange(fields.copy(ky10RxNo = it)) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
            KyField(
                label = "แพทย์ผู้สั่ง",
                value = fields.ky10Doctor,
                onValueChange = { onChange(fields.copy(ky10Doctor = it)) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
        }
        KyField(
            label = "คงเหลือ",
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(
            label = "ขย.12 — ใบสั่งแพทย์",
            sublabel = lines.joinToString(", ") { it.drug.name },
            badgeColor = MaterialTheme.colorScheme.primary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KyField(
                label = "เลขใบสั่ง *",
                value = fields.ky12RxNo,
                onValueChange = { onChange(fields.copy(ky12RxNo = it)) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
            KyField(
                label = "ผู้ป่วย *",
                value = fields.ky12PatientName,
                onValueChange = { onChange(fields.copy(ky12PatientName = it)) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KyField(
                label = "แพทย์",
                value = fields.ky12Doctor,
                onValueChange = { onChange(fields.copy(ky12Doctor = it)) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
            KyField(
                label = "โรงพยาบาล",
                value = fields.ky12Hospital,
                onValueChange = { onChange(fields.copy(ky12Hospital = it)) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
        }
        KyField(
            label = "สถานะ",
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
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
