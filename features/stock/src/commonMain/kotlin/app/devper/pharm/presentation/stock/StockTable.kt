package app.devper.pharm.presentation.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.StockStatus
import app.devper.pharm.ui.designsystem.KyBadge
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmActionTone
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
internal fun StockTable(
    drugs: List<Drug>,
    callbacks: StockCallbacks,
    modifier: Modifier = Modifier,
    emptySearching: Boolean = false,
) {
    val t = pharmTokens
    val s = pharmStrings
    val columns = remember(callbacks, t, s) {
        listOf(
        PharmTableColumn<Drug>(
            header = s.expiryHeaderDrugName,
            weight = 2.4f,
            cell = { drug ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = drug.name,
                        style = PharmText.bodySm.copy(fontWeight = FontWeight.Medium),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    drug.regNo?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            text = it,
                            style = PharmText.micro.copy(
                                color = t.colors.fgMuted,
                                fontFamily = FontFamily.Monospace,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            },
        ),
        PharmTableColumn(
            header = s.stockHeaderGeneric,
            weight = 1.6f,
            hideInCompact = true,
            cell = { drug ->
                Text(
                    text = drug.genericName.orEmpty(),
                    style = PharmText.meta,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = s.stockHeaderSize,
            weight = 0.8f,
            hideInCompact = true,
            cell = { drug ->
                Text(
                    text = drug.strength.orEmpty(),
                    style = PharmText.meta,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = s.stockHeaderCategory,
            weight = 1.0f,
            cell = { drug -> TypeBadge(drug) },
        ),
        PharmTableColumn(
            header = s.stockHeaderReports,
            weight = 1.2f,
            cell = { drug -> KyBadgesCell(drug) },
        ),
        PharmTableColumn(
            header = s.importsFormHeaderCostPrice,
            weight = 0.8f,
            align = PharmColumnAlign.End,
            hideInCompact = true,
            cell = { drug ->
                Text(
                    text = fmtBaht(drug.costPrice.amount),
                    style = PharmText.meta.copy(color = t.colors.fg3),
                )
            },
        ),
        PharmTableColumn(
            header = s.importsFormHeaderSellPrice,
            weight = 0.8f,
            align = PharmColumnAlign.End,
            cell = { drug ->
                Text(
                    text = fmtBaht(drug.sellPrice.amount),
                    style = PharmText.bodySm.copy(fontWeight = FontWeight.SemiBold),
                )
            },
        ),
        PharmTableColumn(
            header = s.stockHeaderStock,
            weight = 1.0f,
            align = PharmColumnAlign.End,
            cell = { drug -> StockQtyCell(drug) },
        ),
        PharmTableColumn(
            header = s.commonStatus,
            weight = 0.9f,
            cell = { drug -> StockStatusBadge(drug) },
        ),
        PharmTableColumn(
            header = s.stockHeaderBarcode,
            weight = 1.2f,
            hideInCompact = true,
            cell = { drug ->
                Text(
                    text = drug.barcode.orEmpty(),
                    style = PharmText.micro.copy(
                        color = t.colors.fgMuted,
                        fontFamily = FontFamily.Monospace,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = s.customersHeaderActions,
            weight = 0.6f,
            align = PharmColumnAlign.End,
            cell = { drug -> StockRowActions(drug = drug, callbacks = callbacks) },
        ),
        )
    }

    PharmTable(
        rows = drugs,
        columns = columns,
        key = { it.id },
        modifier = modifier,
        onRowClick = { drug -> callbacks.onEditDrug(drug) },
        rowHeight = 52.dp,
        emptyContent = {
            if (emptySearching) {
                PharmEmptyState(
                    icon = PharmIcons.Search,
                    title = pharmStrings.stockListNotFound,
                )
            } else {
                PharmEmptyState(
                    icon = PharmIcons.Stock,
                    title = pharmStrings.stockListEmpty,
                )
            }
        },
    )
}

@Composable
private fun TypeBadge(drug: Drug) {
    val type = drug.type?.trim()?.lowercase().orEmpty()
    val (tone, label) = when {
        type.contains("herb") || type.contains("สมุนไพร")    -> PharmBadgeTone.Emerald to pharmStrings.stockBadgeHerb
        type.contains("supp") || type.contains(pharmStrings.stockTypeSupplement) -> PharmBadgeTone.Orange  to pharmStrings.stockTypeSupplement
        else                                                  -> PharmBadgeTone.Purple  to pharmStrings.stockTypeAbbrev
    }
    PharmBadge(text = label, tone = tone, size = PharmBadgeSize.Sm)
}

@Composable
private fun KyBadgesCell(drug: Drug) {
    val t = pharmTokens
    val forms = drug.reportTypes.mapNotNull { rt ->
        when (rt.lowercase()) {
            "ky9" -> 9
            "ky10" -> 10
            "ky11" -> 11
            "ky12" -> 12
            else -> null
        }
    }.distinct().sorted()
    if (forms.isEmpty()) {
        Text("—", style = PharmText.micro.copy(color = t.colors.fgMuted))
    } else {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            forms.forEach { KyBadge(form = it) }
        }
    }
}

@Composable
private fun StockQtyCell(drug: Drug) {
    val t = pharmTokens
    val stockColor = when {
        drug.stock.value < 0 -> t.colors.dangerFg
        drug.stockStatus == StockStatus.OutOrOversold -> t.colors.warningFg
        drug.stockStatus == StockStatus.Low -> t.colors.warningFg
        else -> t.colors.fg1
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(
            text = drug.stock.toString(),
            style = PharmText.bodySm.copy(color = stockColor, fontWeight = FontWeight.SemiBold),
        )
        Text(
            text = drug.unit.orEmpty(),
            style = PharmText.micro.copy(color = t.colors.fgMuted),
        )
    }
}

@Composable
private fun StockStatusBadge(drug: Drug) {
    val (tone, label) = when (drug.stockStatus) {
        StockStatus.OutOrOversold ->
            if (drug.stock.value < 0) PharmBadgeTone.Red to pharmStrings.stockMetricBackorder
            else PharmBadgeTone.Amber to pharmStrings.stockStatusOut
        StockStatus.Low -> PharmBadgeTone.Amber to pharmStrings.stockStatusLow
        StockStatus.Healthy -> PharmBadgeTone.Green to pharmStrings.stockStatusNormal
    }
    PharmBadge(text = label, tone = tone, size = PharmBadgeSize.Sm)
}

@Composable
private fun StockRowActions(drug: Drug, callbacks: StockCallbacks) {
    val s = pharmStrings
    val actions = remember(drug.id, callbacks, s) {
        listOf(
            PharmAction(
                label = s.commonEdit,
                icon = PharmIcons.Pencil,
                tone = PharmActionTone.Primary,
                onClick = { callbacks.onEditDrug(drug) },
            ),
            PharmAction(
                label = s.labelsLotPrefix,
                icon = PharmIcons.Stock,
                onClick = { callbacks.onOpenLots(drug) },
            ),
            PharmAction(
                label = s.stockActionAdjust,
                icon = PharmIcons.Pencil,
                tone = PharmActionTone.Success,
                onClick = { callbacks.onOpenAdjust(drug) },
            ),
            PharmAction(
                label = s.stockActionHistory,
                icon = PharmIcons.Movements,
                onClick = { callbacks.onOpenHistory(drug) },
            ),
        )
    }
    PharmActionMenu(actions = actions)
}
