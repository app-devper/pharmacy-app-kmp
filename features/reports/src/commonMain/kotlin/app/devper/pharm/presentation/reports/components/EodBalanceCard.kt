package app.devper.pharm.presentation.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import kotlin.math.abs

@Composable
internal fun EodBalanceCard(report: EodReport) {
    val t = pharmTokens
    val drift = report.netCash - report.totalSales
    val balanced = abs(drift) < 0.01
    val bg = if (balanced) t.colors.successBg else t.colors.dangerBg
    val fg = if (balanced) t.colors.successFg else t.colors.dangerFg
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(bg, t.shapes.lg)
            .border(1.dp, fg.copy(alpha = 0.4f), t.shapes.lg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = if (balanced) PharmIcons.Check else PharmIcons.Warning,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(20.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (balanced) "ลิ้นชักตรงกับยอดขาย" else "ลิ้นชักไม่ตรงกับยอดขาย",
                style = PharmText.h3.copy(color = fg),
            )
            Text(
                text = "เงินเข้าลิ้นชัก ${fmtBaht(report.netCash)} · ยอดขาย ${fmtBaht(report.totalSales)}",
                style = PharmText.meta.copy(color = fg),
            )
        }
        if (!balanced) {
            Text(
                text = (if (drift > 0) "+${fmtBaht(drift)}" else fmtBaht(drift)),
                style = PharmText.total.copy(color = fg),
            )
        }
    }
}
