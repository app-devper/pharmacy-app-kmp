package app.devper.pharm.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.format.formatBaht
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@Composable
internal fun ReportsRecentSalesSection(recent: List<SaleSummary>, modifier: Modifier = Modifier) {
    val t = pharmTokens
    val visible = recent.take(5)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface, t.shapes.lg)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(text = "บิลล่าสุด", style = PharmText.h3)
        if (visible.isEmpty()) {
            Text(text = "ยังไม่มีบิล", style = PharmText.meta)
        } else {
            visible.forEach { sale -> RecentSaleRow(sale) }
        }
    }
}

@Composable
private fun RecentSaleRow(sale: SaleSummary) {
    val t = pharmTokens
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = sale.billNo.ifBlank { sale.id.take(8) },
                style = PharmText.bodySm.tabular().copy(
                    color = t.colors.fg3,
                    fontWeight = FontWeight.Medium,
                ),
                modifier = Modifier.weight(1.2f),
                maxLines = 1,
            )
            Text(
                text = sale.customerName.ifBlank { "ลูกค้าทั่วไป" },
                style = PharmText.bodySm.copy(color = t.colors.fg2),
                modifier = Modifier.weight(1.4f),
                maxLines = 1,
            )
            Text(
                text = sale.soldAt.take(19).replace('T', ' '),
                style = PharmText.micro.tabular().copy(color = t.colors.fg3),
                modifier = Modifier.weight(1.2f),
                maxLines = 1,
            )
            if (sale.voided) {
                PharmStatusBadge(status = PharmStatus.Voided)
            }
            Text(
                text = "฿${formatBaht(sale.total)}",
                style = PharmText.bodySm.tabular().copy(
                    color = if (sale.voided) t.colors.fgMuted else t.colors.accent,
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier.weight(0.9f),
                textAlign = TextAlign.End,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(t.colors.divider),
        )
    }
}
