package app.devper.pharm.presentation.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun EodPaymentBreakdown(report: EodReport, onPrint: () -> Unit) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface, t.shapes.lg)
            .border(1.dp, t.colors.accent, t.shapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = PharmIcons.Check,
                contentDescription = null,
                tint = t.colors.successFg,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = "ปิดรอบ EOD เรียบร้อย — วันที่ ${report.date.ifBlank { "วันนี้" }}",
                style = PharmText.h2,
                modifier = Modifier.weight(1f),
            )
            PharmButton(
                label = "พิมพ์",
                onClick = onPrint,
                variant = PharmButtonVariant.Outline,
                size = PharmButtonSize.Sm,
                leadingIcon = {
                    Icon(
                        imageVector = PharmIcons.Print,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                },
            )
        }
        EodReceiptLine(label = "ยอดขายสุทธิ", value = fmtBaht(report.totalSales))
        EodReceiptLine(label = "จำนวนบิล", value = "${report.billCount} บิล")
        EodReceiptLine(label = "ส่วนลดรวม", value = fmtBaht(report.totalDiscount))
        EodReceiptLine(label = "รับเงิน", value = fmtBaht(report.totalReceived))
        EodReceiptLine(label = "ทอนเงิน", value = fmtBaht(report.totalChange))
        Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
            EodReceiptLine(label = "เงินเข้าลิ้นชัก", value = fmtBaht(report.netCash), bold = true)
        }
    }
}

@Composable
private fun EodReceiptLine(label: String, value: String, bold: Boolean = false) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = PharmText.meta.copy(color = t.colors.fg2))
        Text(
            text = value,
            style = if (bold) PharmText.total else PharmText.bodySm.copy(color = t.colors.fg1),
        )
    }
}
