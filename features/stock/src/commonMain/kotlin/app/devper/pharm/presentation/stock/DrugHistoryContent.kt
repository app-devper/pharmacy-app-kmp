package app.devper.pharm.presentation.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.MovementType
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.presentation.stock.i18n.localizeStock
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DrugHistoryContent(
    state: DrugHistoryUiState,
    onBack: () -> Unit,
    onDismissError: () -> Unit,
) {
    val t = pharmTokens

    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(title = "ประวัติสต็อก", subtitle = state.drugName, onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            PharmListResultLine(total = state.items.size, noun = "รายการ")
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

            when {
                state.loading && state.items.isEmpty() -> PharmListSkeleton()
                else -> DrugHistoryTable(items = state.items)
            }
        }
    }

    ErrorBottomSheet(message = state.errorState?.localizeStock(pharmStrings), onDismiss = onDismissError)
}

@Composable
private fun DrugHistoryTable(items: List<StockMovement>) {
    val columns = remember {
        listOf(
            PharmTableColumn<StockMovement>(
                header = "เวลา",
                weight = 1.4f,
                cell = { m -> TimeCell(m) },
            ),
            PharmTableColumn(
                header = "ประเภท",
                weight = 1.2f,
                compactTitle = true,
                cell = { m -> TypeCell(m) },
            ),
            PharmTableColumn(
                header = "จำนวน",
                weight = 0.8f,
                align = PharmColumnAlign.End,
                cell = { m -> QtyCell(m) },
            ),
            PharmTableColumn(
                header = "อ้างอิง",
                weight = 2.0f,
                cell = { m -> ReferenceCell(m) },
            ),
        )
    }
    PharmTable(
        rows = items,
        columns = columns,
        key = { "${it.type.wire}::${it.id}" },
        rowHeight = 52.dp,
        emptyContent = {
            PharmEmptyState(icon = PharmIcons.Movements, title = "ยังไม่มีประวัติสต็อกของยานี้")
        },
    )
}

@Composable
private fun TimeCell(m: StockMovement) {
    val t = pharmTokens
    Text(
        text = m.at.take(19).replace('T', ' '),
        style = PharmText.micro.copy(color = t.colors.fg3, fontFeatureSettings = "tnum"),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun TypeCell(m: StockMovement) {
    PharmBadge(text = m.type.label, tone = m.type.tone(), size = PharmBadgeSize.Sm)
}

private fun MovementType.tone(): PharmBadgeTone = when (this) {
    MovementType.Import     -> PharmBadgeTone.Green
    MovementType.Sale       -> PharmBadgeTone.Indigo
    MovementType.Return     -> PharmBadgeTone.Emerald
    MovementType.Adjustment -> PharmBadgeTone.Amber
    MovementType.Writeoff   -> PharmBadgeTone.Red
}

@Composable
private fun QtyCell(m: StockMovement) {
    val t = pharmTokens
    val color = when {
        m.delta > 0 -> t.colors.successFg
        m.delta < 0 -> t.colors.dangerFg
        else        -> t.colors.fg2
    }
    val sign = when {
        m.delta > 0 -> "+"
        m.delta < 0 -> "−"
        else        -> ""
    }
    val magnitude = if (m.delta < 0) (-m.delta).toString() else m.delta.toString()
    Text(
        text = "$sign$magnitude",
        style = PharmText.bodySm.copy(color = color, fontWeight = FontWeight.SemiBold, fontFeatureSettings = "tnum"),
        maxLines = 1,
    )
}

@Composable
private fun ReferenceCell(m: StockMovement) {
    val t = pharmTokens
    val display = when {
        m.reference.isNotBlank() && m.note.isNotBlank() -> "${m.reference} · ${m.note}"
        m.reference.isNotBlank() -> m.reference
        m.note.isNotBlank() -> m.note
        else -> "—"
    }
    Text(
        text = display,
        style = PharmText.micro.copy(color = t.colors.fg3, fontFamily = FontFamily.Monospace),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

private val sampleHistory = listOf(
    StockMovement("1", MovementType.Import, "d1", "พาราเซตามอล 500mg", 240, "IMP-260516-002", "by: ภ. ปรียา", "2026-05-17T13:30:00"),
    StockMovement("2", MovementType.Sale, "d1", "พาราเซตามอล 500mg", -10, "SC-260516-014", "by: ภ. ปรียา", "2026-05-17T14:42:00"),
    StockMovement("3", MovementType.Adjustment, "d1", "พาราเซตามอล 500mg", -2, "SC-260516-001", "นับสต็อก", "2026-05-17T12:15:00"),
    StockMovement("4", MovementType.Writeoff, "d1", "พาราเซตามอล 500mg", -8, "EXP-260516-001", "หมดอายุ", "2026-05-16T17:45:00"),
)

@Preview
@Composable
private fun DrugHistoryContent_Loaded_Preview() {
    PharmacyTheme {
        DrugHistoryContent(
            state = DrugHistoryUiState(drugName = "พาราเซตามอล 500mg", items = sampleHistory),
            onBack = {},
            onDismissError = {},
        )
    }
}

@Preview
@Composable
private fun DrugHistoryContent_Empty_Preview() {
    PharmacyTheme {
        DrugHistoryContent(
            state = DrugHistoryUiState(drugName = "พาราเซตามอล 500mg", items = emptyList()),
            onBack = {},
            onDismissError = {},
        )
    }
}

@Preview
@Composable
private fun DrugHistoryContent_Loading_Preview() {
    PharmacyTheme {
        DrugHistoryContent(
            state = DrugHistoryUiState(drugName = "พาราเซตามอล 500mg", loading = true),
            onBack = {},
            onDismissError = {},
        )
    }
}
