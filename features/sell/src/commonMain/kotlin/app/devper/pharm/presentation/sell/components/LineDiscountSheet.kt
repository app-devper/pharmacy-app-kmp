package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineDiscountSheet(
    line: CartLine,
    onApply: (key: app.devper.pharm.domain.model.CartLineKey, discount: Double) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val t = pharmTokens
    val factor = line.factor
    val perDisplayDiscount = line.discount * factor
    var draft by remember(line.key, perDisplayDiscount) {
        mutableStateOf(if (perDisplayDiscount == 0.0) "" else perDisplayDiscount.toString().trimEnd('0').trimEnd('.'))
    }
    val parsed = draft.toDoubleOrNull()
    val effective = (line.unitPrice - (parsed ?: 0.0)).coerceAtLeast(0.0)
    val invalid = parsed != null && (parsed < 0.0 || parsed > line.unitPrice)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = t.colors.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("ส่วนลดต่อหน่วย", style = PharmText.h1)
            Text(line.drug.name, style = PharmText.meta)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ราคาเดิม", style = PharmText.bodySm.copy(color = t.colors.fg2))
                Text(fmtBaht(line.unitPrice), style = PharmText.bodySm.tabular())
            }

            FormField(
                label = "ส่วนลด (บาท)",
                error = if (invalid) "0 ≤ ส่วนลด ≤ ${line.unitPrice}" else null,
            ) {
                PharmTextField(
                    value = draft,
                    onValueChange = { draft = it.filter { c -> c.isDigit() || c == '.' } },
                    placeholder = "0",
                    keyboardType = KeyboardType.Decimal,
                    isError = invalid,
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ราคาหลังหัก", style = PharmText.body)
                Text(fmtBaht(effective), style = PharmText.price)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PharmButton(
                    label = "ล้างส่วนลด",
                    onClick = { onApply(line.key, 0.0) },
                    variant = PharmButtonVariant.Ghost,
                    size = PharmButtonSize.Md,
                    modifier = Modifier.weight(1f),
                )
                PharmButton(
                    label = "บันทึก",
                    onClick = {
                        val perBase = (parsed ?: 0.0) / factor
                        onApply(line.key, perBase)
                    },
                    enabled = !invalid,
                    size = PharmButtonSize.Md,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
