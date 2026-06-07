package app.devper.pharm.presentation.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.format.localDateTimeToBuddhist
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun EodBillsHeader(count: Int) {
    val t = pharmTokens
    Text(
        text = "บิลในวัน · $count รายการ",
        style = PharmText.h3.copy(color = t.colors.fg1),
    )
}

@Composable
internal fun EodBillRow(bill: SaleSummary) {
    val t = pharmTokens
    val totalColor = if (bill.voided) t.colors.fgMuted else t.colors.price
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(t.colors.surface, t.shapes.md)
            .border(1.dp, t.colors.borderSubtle, t.shapes.md)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = bill.billNo.ifBlank { bill.id.take(8) },
                style = PharmText.bodySm,
            )
            Text(
                text = localDateTimeToBuddhist(bill.soldAt),
                style = PharmText.micro.copy(color = t.colors.fgMuted),
            )
        }
        if (bill.voided) {
            PharmBadge(text = "ยกเลิก", tone = PharmBadgeTone.Gray)
        }
        Text(
            text = fmtBaht(bill.total),
            style = PharmText.price.copy(color = totalColor),
        )
    }
}

@Composable
internal fun EmptyEod() {
    val t = pharmTokens
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(t.shapes.lg)
            .background(t.colors.surface, t.shapes.lg)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "ไม่มีข้อมูล", style = PharmText.h2)
            Text(
                text = "ลองเลือกวันที่อื่นเพื่อดูยอดขาย",
                style = PharmText.meta.copy(color = t.colors.fgMuted),
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
