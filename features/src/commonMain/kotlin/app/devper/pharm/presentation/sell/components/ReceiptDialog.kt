package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun ReceiptDialog(
    sale: Sale,
    received: Double,
    onDismiss: () -> Unit,
    onVoid: (() -> Unit)? = null,
    onPrint: (() -> Unit)? = null,
) {
    val t = pharmTokens

    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = "ออกใบเสร็จสำเร็จ",
        subtitle = "เลขที่ ${sale.billNo}",
        size = PharmModalSize.Md,
        footer = {
            if (onVoid != null) {
                PharmButton(
                    label = "ยกเลิกบิล",
                    onClick = onVoid,
                    variant = PharmButtonVariant.Ghost,
                    size = PharmButtonSize.Md,
                )
                Box(modifier = Modifier.size(1.dp))
            }
            if (onPrint != null) {
                PharmButton(
                    label = "🖨 พิมพ์",
                    onClick = onPrint,
                    variant = PharmButtonVariant.Secondary,
                    size = PharmButtonSize.Md,
                )
            }
            PharmButton(
                label = "บิลใหม่",
                onClick = onDismiss,
                size = PharmButtonSize.Md,
            )
        },
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(t.shapes.md)
                .background(t.colors.bgPage)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "ร้านยา เฮลท์ตี้ฟาร์ม",
                style = PharmText.h3,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "เลขที่ ${sale.billNo}",
                style = PharmText.micro,
            )
            HorizontalRule()
            ReceiptRow("ยอดรวม", fmtBaht(sale.total + sale.discount))
            if (sale.discount > 0) ReceiptRow("ส่วนลด", "−${fmtBaht(sale.discount)}", isDiscount = true)
            ReceiptRow("ยอดสุทธิ", fmtBaht(sale.total), emphasis = true)
            ReceiptRow("รับเงิน", fmtBaht(received))
            ReceiptRow("เงินทอน", fmtBaht(sale.change), emphasis = true, change = true)
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    emphasis: Boolean = false,
    isDiscount: Boolean = false,
    change: Boolean = false,
) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = PharmText.bodySm.copy(color = t.colors.fg2),
            modifier = Modifier.weight(1f),
        )
        val color = when {
            isDiscount -> t.colors.discount
            change -> t.colors.successFg
            emphasis -> t.colors.fg1
            else -> t.colors.fg1
        }
        Text(
            text = value,
            style = PharmText.bodySm.copy(
                color = color,
                fontWeight = if (emphasis) FontWeight.Bold else FontWeight.Normal,
                fontFeatureSettings = "tnum",
            ),
        )
    }
}

@Composable
private fun HorizontalRule() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .size(height = 1.dp, width = 1.dp)
            .background(pharmTokens.colors.border),
    )
}
