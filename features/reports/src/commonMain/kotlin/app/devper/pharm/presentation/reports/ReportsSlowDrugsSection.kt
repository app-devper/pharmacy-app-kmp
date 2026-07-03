package app.devper.pharm.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.SlowDrug
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmStaticTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@Composable
internal fun ReportsSlowDrugsSection(rows: List<SlowDrug>, modifier: Modifier = Modifier) {
    val t = pharmTokens

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(t.colors.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            val s = pharmStrings
            Text(text = s.reportsProfitNoMovement, style = PharmText.h3)
            Text(text = "(${rows.size} ${s.movementsCountNoun})", style = PharmText.meta)
        }
        PharmStaticTable(
            rows = rows.take(10),
            columns = slowDrugColumns(),
            emptyText = pharmStrings.bulkImportEmptyDropped,
        )
    }
}

@Composable
private fun slowDrugColumns(): List<PharmTableColumn<SlowDrug>> {
    val t = pharmTokens
    val s = pharmStrings
    return listOf(
        PharmTableColumn(
            header = s.reportsDrugWord,
            weight = 1f,
            cell = { row ->
                Text(
                    text = row.drugName,
                    style = PharmText.bodySm.copy(color = t.colors.fg2),
                    maxLines = 1,
                )
            },
        ),
        PharmTableColumn(
            header = s.expiryHeaderRemaining,
            weight = 0.6f,
            align = PharmColumnAlign.End,
            cell = { row ->
                Text(
                    text = "${row.stock} ${row.unit}",
                    style = PharmText.bodySm.tabular().copy(color = t.colors.warningFg),
                )
            },
        ),
    )
}
