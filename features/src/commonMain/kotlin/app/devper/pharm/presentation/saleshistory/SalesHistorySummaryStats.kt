package app.devper.pharm.presentation.saleshistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun SalesHistorySummaryStats(
    sales: List<SaleSummary>,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val total = sales.size
    val subtotal = sales.filterNot { it.voided }.sumOf { it.total }
    val voided = sales.count { it.voided }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatChunk(number = total.toString(), label = "รายการ")
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "ยอดรวม", style = PharmText.bodySm.copy(color = t.colors.fg3))
            Text(
                text = fmtBaht(subtotal),
                style = PharmText.bodySm.copy(
                    color = t.colors.accent,
                    fontWeight = FontWeight.SemiBold,
                    fontFeatureSettings = "tnum",
                ),
            )
        }
        if (voided > 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = voided.toString(),
                    style = PharmText.bodySm.copy(
                        color = t.colors.dangerFg,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                Text(
                    text = "ยกเลิก",
                    style = PharmText.bodySm.copy(color = t.colors.dangerFg),
                )
            }
        }
    }
}

@Composable
private fun StatChunk(number: String, label: String) {
    val t = pharmTokens
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = number,
            style = PharmText.bodySm.copy(
                color = t.colors.fg1,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        Text(text = label, style = PharmText.bodySm.copy(color = t.colors.fg3))
    }
}
