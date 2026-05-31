package app.devper.pharm.presentation.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun StockTable(
    drugs: List<Drug>,
    callbacks: StockCallbacks,
    modifier: Modifier = Modifier,
    emptySearching: Boolean = false,
) {
    val t = pharmTokens
    val columns = remember(callbacks, t) {
        listOf(
        PharmTableColumn<Drug>(
            header = "ชื่อยา",
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
            header = "ชื่อสามัญ",
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
            header = "ขนาด",
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
            header = "ประเภท",
            weight = 1.0f,
            cell = { drug -> TypeBadge(drug) },
        ),
        PharmTableColumn(
            header = "รายงาน ขย.",
            weight = 1.2f,
            cell = { drug -> KyBadgesCell(drug) },
        ),
        PharmTableColumn(
            header = "ราคาทุน",
            weight = 0.8f,
            align = PharmColumnAlign.End,
            hideInCompact = true,
            cell = { drug ->
                Text(
                    text = fmtBaht(drug.costPrice),
                    style = PharmText.meta.copy(color = t.colors.fg3),
                )
            },
        ),
        PharmTableColumn(
            header = "ราคาขาย",
            weight = 0.8f,
            align = PharmColumnAlign.End,
            cell = { drug ->
                Text(
                    text = fmtBaht(drug.sellPrice),
                    style = PharmText.bodySm.copy(fontWeight = FontWeight.SemiBold),
                )
            },
        ),
        PharmTableColumn(
            header = "สต็อก",
            weight = 1.0f,
            align = PharmColumnAlign.End,
            cell = { drug -> StockQtyCell(drug) },
        ),
        PharmTableColumn(
            header = "สถานะ",
            weight = 0.9f,
            cell = { drug -> StockStatusBadge(drug) },
        ),
        PharmTableColumn(
            header = "บาร์โค้ด",
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
            header = "จัดการ",
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
        rowHeight = 60.dp,
        emptyContent = {
            Text(
                text = if (emptySearching) "ไม่พบยาที่ค้นหา" else "ยังไม่มีรายการยาในคลัง",
                style = PharmText.meta,
            )
        },
    )
}

@Composable
private fun TypeBadge(drug: Drug) {
    val type = drug.type?.trim()?.lowercase().orEmpty()
    val (tone, label) = when {
        type.contains("herb") || type.contains("สมุนไพร")    -> PharmBadgeTone.Emerald to "สมุนไพร"
        type.contains("supp") || type.contains("อาหารเสริม") -> PharmBadgeTone.Orange  to "อาหารเสริม"
        else                                                  -> PharmBadgeTone.Purple  to "แผนปัจจุบัน"
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
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            forms.forEach { KyBadge(form = it) }
        }
    }
}

@Composable
private fun StockQtyCell(drug: Drug) {
    val t = pharmTokens
    val stockColor = when {
        drug.stock < 0 -> t.colors.dangerFg
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
            if (drug.stock < 0) PharmBadgeTone.Red to "ค้างส่ง"
            else PharmBadgeTone.Amber to "หมด"
        StockStatus.Low -> PharmBadgeTone.Amber to "ใกล้หมด"
        StockStatus.Healthy -> PharmBadgeTone.Green to "ปกติ"
    }
    PharmBadge(text = label, tone = tone, size = PharmBadgeSize.Sm)
}

@Composable
private fun StockRowActions(drug: Drug, callbacks: StockCallbacks) {
    val actions = remember(drug.id, callbacks) {
        listOf(
            PharmAction(
                label = "แก้ไข",
                icon = PharmIcons.Pencil,
                tone = PharmActionTone.Primary,
                onClick = { callbacks.onEditDrug(drug) },
            ),
            PharmAction(
                label = "ล็อต",
                icon = PharmIcons.Stock,
                onClick = { callbacks.onOpenLots(drug) },
            ),
            PharmAction(
                label = "ปรับสต็อก",
                icon = PharmIcons.Pencil,
                tone = PharmActionTone.Success,
                onClick = { callbacks.onOpenAdjust(drug) },
            ),
            PharmAction(
                label = "ประวัติ",
                icon = PharmIcons.Movements,
                onClick = { callbacks.onOpenHistory(drug) },
            ),
        )
    }
    PharmActionMenu(actions = actions)
}
