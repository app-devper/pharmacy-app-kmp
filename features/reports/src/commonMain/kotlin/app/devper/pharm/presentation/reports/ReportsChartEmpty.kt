package app.devper.pharm.presentation.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun ReportsChartEmpty(height: Dp, modifier: Modifier = Modifier) {
    val t = pharmTokens
    val s = pharmStrings
    Column(
        modifier = modifier.fillMaxWidth().height(height),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = s.reportsSectionDailySalesEmpty,
            style = PharmText.body.copy(color = t.colors.fg2),
            textAlign = TextAlign.Center,
        )
        Text(
            text = s.reportsEmptyChartHint,
            style = PharmText.meta.copy(color = t.colors.fgMuted),
            textAlign = TextAlign.Center,
        )
    }
}
