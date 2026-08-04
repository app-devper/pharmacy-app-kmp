package app.devper.pharm.presentation.planning

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.StockStatus
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@Composable
internal fun LowStockTable(
    drugs: List<Drug>,
    callbacks: LowStockCallbacks,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    val columns = remember(s) {
        listOf(
            PharmTableColumn<Drug>(
                header = s.expiryHeaderDrugName,
                weight = 2.4f,
                compactTitle = true,
                cell = { drug -> LowStockNameCell(drug) },
            ),
            PharmTableColumn(
                header = s.expiryHeaderRemaining,
                weight = 1.0f,
                align = PharmColumnAlign.End,
                cell = { drug -> LowStockCurrentCell(drug) },
            ),
            PharmTableColumn(
                header = s.planningHeaderMin,
                weight = 0.9f,
                align = PharmColumnAlign.End,
                hideInCompact = true,
                cell = { drug -> LowStockMinCell(drug) },
            ),
            PharmTableColumn(
                header = s.commonUnit,
                weight = 0.8f,
                hideInCompact = true,
                cell = { drug -> LowStockUnitCell(drug) },
            ),
            PharmTableColumn(
                header = s.commonStatus,
                compactTrailing = true,
                weight = 1.0f,
                align = PharmColumnAlign.End,
                cell = { drug -> LowStockStatusCell(drug) },
            ),
        )
    }

    PharmTable(
        rows = drugs,
        columns = columns,
        key = { it.id },
        modifier = modifier,
        onRowClick = { callbacks.onRowClick(it) },
        emptyContent = {
            Text(text = s.planningLowStockEmpty, style = PharmText.meta)
        },
    )
}

@Composable
private fun LowStockNameCell(drug: Drug) {
    val t = pharmTokens
    val secondary = listOfNotNull(drug.genericName?.takeIf { it.isNotBlank() }, drug.unit?.takeIf { it.isNotBlank() })
        .joinToString(" · ")
    androidx.compose.foundation.layout.Column(
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = drug.name,
            style = PharmText.bodySm.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (secondary.isNotEmpty()) {
            Text(
                text = secondary,
                style = PharmText.micro.copy(color = t.colors.fg3),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun LowStockCurrentCell(drug: Drug) {
    val t = pharmTokens
    val color = when (drug.stockStatus) {
        StockStatus.OutOrOversold -> t.colors.dangerFg
        StockStatus.Low           -> t.colors.warningFg
        StockStatus.Healthy       -> t.colors.fg1
    }
    Text(
        text = drug.stock.toString(),
        style = PharmText.bodySm.copy(fontWeight = FontWeight.SemiBold, color = color).tabular(),
        maxLines = 1,
    )
}

@Composable
private fun LowStockMinCell(drug: Drug) {
    val t = pharmTokens
    val text = if (drug.minStock.isPositive) drug.minStock.toString() else "—"
    Text(
        text = text,
        style = PharmText.bodySm.copy(color = if (drug.minStock.isPositive) t.colors.fg2 else t.colors.fgMuted).tabular(),
        maxLines = 1,
    )
}

@Composable
private fun LowStockUnitCell(drug: Drug) {
    val t = pharmTokens
    val unit = drug.unit?.takeIf { it.isNotBlank() } ?: "—"
    Text(
        text = unit,
        style = PharmText.bodySm.copy(color = if (drug.unit.isNullOrBlank()) t.colors.fgMuted else t.colors.fg2),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun LowStockStatusCell(drug: Drug) {
    val status = when (drug.stockStatus) {
        StockStatus.OutOrOversold -> PharmStatus.OutOfStock
        StockStatus.Low           -> PharmStatus.LowStock
        StockStatus.Healthy       -> PharmStatus.Normal
    }
    PharmStatusBadge(status = status)
}
