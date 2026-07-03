package app.devper.pharm.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.TopDrug
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@Composable
internal fun ReportsTopDrugsSection(rows: List<TopDrug>, modifier: Modifier = Modifier) {
    val t = pharmTokens
    val visible = rows.take(10)
    val maxQty = (visible.maxOfOrNull { it.qtySold } ?: 0).coerceAtLeast(1)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(t.colors.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val s = pharmStrings
        Text(text = s.reportsTopSellingTitle, style = PharmText.h3)
        if (visible.isEmpty()) {
            Text(text = s.reportsEmptyNoData, style = PharmText.meta)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                visible.forEachIndexed { index, row ->
                    TopDrugRow(index = index + 1, row = row, maxQty = maxQty)
                }
            }
        }
    }
}

@Composable
private fun TopDrugRow(index: Int, row: TopDrug, maxQty: Int) {
    val t = pharmTokens
    val ratio = (row.qtySold.toFloat() / maxQty.toFloat()).coerceIn(0f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "$index.",
            style = PharmText.bodySm.tabular().copy(
                color = t.colors.fgMuted,
                textAlign = TextAlign.End,
            ),
            modifier = Modifier.width(24.dp),
        )
        Text(
            text = row.drugName,
            style = PharmText.bodySm.copy(color = t.colors.fg2),
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        Box(
            modifier = Modifier
                .width(128.dp)
                .height(8.dp)
                .clip(t.shapes.pill)
                .background(t.colors.borderSubtle),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(ratio)
                    .background(t.colors.accent, t.shapes.pill),
            )
        }
        Text(
            text = "${row.qtySold}",
            style = PharmText.bodySm.tabular().copy(color = t.colors.fg3),
            modifier = Modifier.widthIn(min = 40.dp),
            textAlign = TextAlign.End,
        )
    }
}
