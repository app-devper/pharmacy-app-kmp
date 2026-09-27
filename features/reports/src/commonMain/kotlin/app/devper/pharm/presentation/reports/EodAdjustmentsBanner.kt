package app.devper.pharm.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.EodAdjustments
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

/**
 * A closed day shows its figures as closed; sales, voids, or returns
 * recorded afterwards are Late sale adjustments, summarised here.
 */
@Composable
internal fun EodAdjustmentsBanner(adjustments: EodAdjustments, modifier: Modifier = Modifier) {
    val t = pharmTokens
    Text(
        text = pharmStrings.reportsEodAdjustmentsBanner(
            adjustments.count,
            adjustments.adjustedBillCount,
            fmtBaht(adjustments.adjustedTotalSales),
        ),
        style = PharmText.meta.copy(color = t.colors.warningFg, fontWeight = FontWeight.SemiBold),
        modifier = modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(t.colors.warningBg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    )
}
