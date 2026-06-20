package app.devper.pharm.presentation.stockcount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

data class StockCountFormRow(
    val drug: Drug,
    val countedText: String,
    val counted: Int?,
    val delta: Int?,
    val highlighted: Boolean,
)

@Composable
internal fun StockCountFormTable(
    rows: List<StockCountFormRow>,
    callbacks: StockCountFormCallbacks,
    modifier: Modifier = Modifier,
    emptySearching: Boolean = false,
) {
    val s = pharmStrings
    val columns = remember(callbacks, s) {
        listOf(
            PharmTableColumn<StockCountFormRow>(
                header = s.expiryHeaderDrugName,
                weight = 2.4f,
                cell = { row -> StockCountDrugCell(row) },
            ),
            PharmTableColumn(
                header = s.stockCountFormInSystem,
                weight = 1.0f,
                align = PharmColumnAlign.End,
                cell = { row -> StockCountSystemCell(row) },
            ),
            PharmTableColumn(
                header = s.stockCountFormCounted,
                weight = 1.1f,
                align = PharmColumnAlign.End,
                cell = { row -> StockCountInputCell(row = row, callbacks = callbacks) },
            ),
            PharmTableColumn(
                header = s.stockCountFormDelta,
                weight = 0.8f,
                align = PharmColumnAlign.End,
                cell = { row -> StockCountDeltaCell(row) },
            ),
        )
    }

    PharmTable(
        rows = rows,
        columns = columns,
        key = { it.drug.id },
        modifier = modifier,
        rowHeight = 52.dp,
        emptyContent = {
            Text(
                text = if (emptySearching) s.stockCountFormEmptySearching else s.stockCountFormEmptyDefault,
                style = PharmText.meta,
            )
        },
    )
}

@Composable
private fun StockCountDrugCell(row: StockCountFormRow) {
    val t = pharmTokens
    val bg = if (row.highlighted) t.colors.warningBg.copy(alpha = 0.35f) else Color.Transparent
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = row.drug.name,
                style = PharmText.bodySm.copy(color = t.colors.fg1, fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val unit = row.drug.unit?.takeIf { it.isNotBlank() }
            if (unit != null) {
                Text(
                    text = pharmStrings.stockCountFormUnitLabel(unit),
                    style = PharmText.micro.copy(color = t.colors.fgMuted),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun StockCountSystemCell(row: StockCountFormRow) {
    val t = pharmTokens
    val bg = if (row.highlighted) t.colors.warningBg.copy(alpha = 0.35f) else Color.Transparent
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Text(
            text = row.drug.stock.toString(),
            style = PharmText.bodySm.copy(color = t.colors.fg3),
        )
    }
}

@Composable
private fun StockCountInputCell(row: StockCountFormRow, callbacks: StockCountFormCallbacks) {
    val t = pharmTokens
    val bg = if (row.highlighted) t.colors.warningBg.copy(alpha = 0.35f) else Color.Transparent
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Box(modifier = Modifier.widthIn(min = 72.dp, max = 110.dp)) {
            PharmTextField(
                value = row.countedText,
                onValueChange = { callbacks.onCountedChange(row.drug.id, it) },
                placeholder = "—",
                keyboardType = KeyboardType.Number,
            )
        }
    }
}

@Composable
private fun StockCountDeltaCell(row: StockCountFormRow) {
    val t = pharmTokens
    val bg = if (row.highlighted) t.colors.warningBg.copy(alpha = 0.35f) else Color.Transparent
    val delta = row.delta
    val color = when {
        delta == null    -> t.colors.fgMuted
        delta == 0       -> t.colors.fg3
        delta > 0        -> t.colors.successFg
        else             -> t.colors.dangerFg
    }
    val text = when {
        delta == null    -> "—"
        delta == 0       -> "0"
        delta > 0        -> "+$delta"
        else             -> delta.toString()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                style = PharmText.bodySm.copy(color = color, fontWeight = FontWeight.SemiBold),
            )
        }
    }
}
