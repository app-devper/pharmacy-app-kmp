package app.devper.pharm.presentation.saleshistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import app.devper.pharm.ui.theme.tabular

@Composable
internal fun SalesHistoryTotalStat(
    sales: List<SaleSummary>,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val subtotal = sales.filterNot { it.voided }.sumOf { it.total.amount }
    val voided = sales.count { it.voided }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "ยอดรวม", style = PharmText.micro.copy(color = t.colors.fg3))
            Text(
                text = fmtBaht(subtotal),
                style = PharmText.micro.copy(
                    color = t.colors.accent,
                    fontWeight = FontWeight.SemiBold,
                ).tabular(),
            )
        }
        if (voided > 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$voided",
                    style = PharmText.micro.copy(color = t.colors.dangerFg, fontWeight = FontWeight.SemiBold).tabular(),
                )
                Text(text = "ยกเลิก", style = PharmText.micro.copy(color = t.colors.dangerFg))
            }
        }
    }
}
