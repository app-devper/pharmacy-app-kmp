package app.devper.pharm.presentation.stockcount.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.stockcount.StockCountDiscrepancy
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun SubmitConfirmModal(
    open: Boolean,
    changedCount: Int,
    totalAbsDelta: Int,
    topDiscrepancies: List<StockCountDiscrepancy>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    PharmModal(
        open = open,
        onDismiss = onCancel,
        size = PharmModalSize.Lg,
        title = "ยืนยันการปรับสต็อก",
        subtitle = "ระบบจะปรับสต็อกตามจำนวนที่นับ — ยืนยันแล้วไม่สามารถย้อนกลับได้",
        footer = {
            PharmButton(
                label = "ยกเลิก",
                onClick = onCancel,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Md,
            )
            PharmButton(
                label = "ยืนยัน",
                onClick = onConfirm,
                variant = PharmButtonVariant.Primary,
                size = PharmButtonSize.Md,
            )
        },
    ) {
        val t = pharmTokens
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                SummaryStat(
                    label = "รายการที่เปลี่ยน",
                    value = "$changedCount",
                    modifier = Modifier.weight(1f),
                )
                SummaryStat(
                    label = "ส่วนต่างรวม (abs)",
                    value = "$totalAbsDelta",
                    highlight = totalAbsDelta > 0,
                    modifier = Modifier.weight(1f),
                )
            }

            if (topDiscrepancies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ส่วนต่างสูงสุด ${topDiscrepancies.size} อันดับ",
                    style = PharmText.micro.copy(color = t.colors.fg3),
                )
                topDiscrepancies.forEach { row -> DiscrepancyRow(row = row) }
            }
        }
    }
}

@Composable
private fun SummaryStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
) {
    val t = pharmTokens
    val valueColor = if (highlight) t.colors.warningFg else t.colors.fg1
    Column(
        modifier = modifier
            .clip(t.shapes.md)
            .background(t.colors.bgPage)
            .border(1.dp, t.colors.borderSubtle, t.shapes.md)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(text = label, style = PharmText.micro.copy(color = t.colors.fg3))
        Text(
            text = value,
            style = PharmText.h2.copy(color = valueColor, fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun DiscrepancyRow(row: StockCountDiscrepancy) {
    val t = pharmTokens
    val deltaColor = when {
        row.delta > 0 -> t.colors.successFg
        row.delta < 0 -> t.colors.dangerFg
        else -> t.colors.fgMuted
    }
    val deltaSign = if (row.delta > 0) "+" else ""
    val unitSuffix = if (row.unit.isBlank()) "" else " ${row.unit}"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = row.drugName,
            style = PharmText.bodySm.copy(color = t.colors.fg1),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${row.counted} / ${row.systemStock}$unitSuffix",
            style = PharmText.bodySm.copy(color = t.colors.fg2),
        )
        Text(
            text = "$deltaSign${row.delta}",
            style = PharmText.bodySm.copy(color = deltaColor, fontWeight = FontWeight.SemiBold),
        )
    }
}
