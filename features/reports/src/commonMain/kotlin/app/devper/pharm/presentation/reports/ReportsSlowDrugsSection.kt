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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.SlowDrug
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@Composable
internal fun ReportsSlowDrugsSection(rows: List<SlowDrug>, modifier: Modifier = Modifier) {
    val t = pharmTokens
    val visible = rows.take(10)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface, t.shapes.lg)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(text = "ยาขายไม่ออก", style = PharmText.h3)
            Text(text = "(${rows.size} รายการ)", style = PharmText.meta)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "ยา", style = PharmText.thead, modifier = Modifier.weight(1f))
            Text(
                text = "คงเหลือ",
                style = PharmText.thead,
                modifier = Modifier.weight(0.6f),
                textAlign = TextAlign.End,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(t.colors.border),
        )
        if (visible.isEmpty()) {
            Text(text = "ไม่มีรายการ", style = PharmText.meta)
        } else {
            visible.forEach { row -> SlowDrugRow(row) }
        }
    }
}

@Composable
private fun SlowDrugRow(row: SlowDrug) {
    val t = pharmTokens
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = row.drugName,
                style = PharmText.bodySm.copy(color = t.colors.fg2),
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )
            Text(
                text = "${row.stock} ${row.unit}",
                style = PharmText.bodySm.tabular().copy(color = t.colors.warningFg),
                modifier = Modifier.weight(0.6f),
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
