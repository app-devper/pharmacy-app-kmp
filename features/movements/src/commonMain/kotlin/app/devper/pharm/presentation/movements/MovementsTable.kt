package app.devper.pharm.presentation.movements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.presentation.movements.i18n.localizedLabel as specLocalizedLabel
import app.devper.pharm.ui.i18n.localizedLabel
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun MovementsTable(
    state: MovementsUiState,
    callbacks: MovementsCallbacks,
    modifier: Modifier = Modifier,
) {
    val rows = state.pageItems
    val s = pharmStrings
    val columns = remember(s) {
        listOf(
            PharmTableColumn<StockMovement>(
                header = s.salesHistoryHeaderTime,
                weight = 1.2f,
                cell = { m -> TimeCell(m) },
            ),
            PharmTableColumn(
                header = s.movementsHeaderType,
                weight = 1.2f,
                cell = { m -> TypeCell(m) },
            ),
            PharmTableColumn(
                header = s.expiryHeaderDrugName,
                weight = 2.0f,
                compactTitle = true,
                cell = { m -> DrugCell(m) },
            ),
            PharmTableColumn(
                header = s.commonQty,
                weight = 0.9f,
                align = PharmColumnAlign.End,
                cell = { m -> QtyCell(m) },
            ),
            PharmTableColumn(
                header = s.movementsHeaderRef,
                weight = 1.4f,
                cell = { m -> ReferenceCell(m) },
            ),
            PharmTableColumn(
                header = s.movementsHeaderBy,
                weight = 1.0f,
                cell = { m -> UserCell(m) },
            ),
        )
    }

    PharmTable(
        rows = rows,
        columns = columns,
        key = { "${it.type.wire}::${it.id}" },
        modifier = modifier,
        rowHeight = 52.dp,
        emptyContent = {
            PharmEmptyState(
                icon = PharmIcons.Movements,
                title = s.movementsEmpty,
            )
        },
        bottomRow = {
            MovementsPaginationRow(state = state, callbacks = callbacks)
        },
    )
}

@Composable
private fun TimeCell(m: StockMovement) {
    val t = pharmTokens
    Text(
        text = m.at.take(19).replace('T', ' '),
        style = PharmText.micro.copy(
            color = t.colors.fg3,
            fontFeatureSettings = "tnum",
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun TypeCell(m: StockMovement) {
    val spec = MovementsTypeCatalog.byMovementType[m.type]
    val label = spec?.specLocalizedLabel(pharmStrings) ?: m.type.localizedLabel(pharmStrings)
    val tone = spec?.tone ?: PharmBadgeTone.Gray
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (spec != null) {
            Icon(
                imageVector = spec.icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = pharmTokens.colors.fg2,
            )
        }
        PharmBadge(text = label, tone = tone, size = PharmBadgeSize.Sm)
    }
}

@Composable
private fun DrugCell(m: StockMovement) {
    val t = pharmTokens
    Text(
        text = m.drugName.ifBlank { "(ไม่ระบุยา)" },
        style = PharmText.bodySm.copy(color = t.colors.fg1),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun QtyCell(m: StockMovement) {
    val t = pharmTokens
    val positive = m.delta > 0
    val color = when {
        positive       -> t.colors.successFg
        m.delta < 0    -> t.colors.dangerFg
        else           -> t.colors.fg2
    }
    val sign = when {
        positive       -> "+"
        m.delta < 0    -> "−"
        else           -> ""
    }
    val magnitude = if (m.delta < 0) (-m.delta).toString() else m.delta.toString()
    Text(
        text = "$sign$magnitude",
        style = PharmText.bodySm.copy(
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontFeatureSettings = "tnum",
        ),
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
        style = PharmText.micro.copy(
            color = t.colors.fg3,
            fontFamily = FontFamily.Monospace,
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun UserCell(m: StockMovement) {
    val t = pharmTokens
    val userText = extractUser(m.note).ifBlank { "—" }
    Text(
        text = userText,
        style = PharmText.bodySm.copy(color = t.colors.fg3),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

private fun extractUser(note: String): String {
    if (note.isBlank()) return ""
    val marker = "by:"
    val ix = note.indexOf(marker)
    if (ix < 0) return ""
    return note.substring(ix + marker.length).trim().takeWhile { it != ' ' && it != '·' }
}

@Composable
private fun MovementsPaginationRow(
    state: MovementsUiState,
    callbacks: MovementsCallbacks,
) {
    val t = pharmTokens
    val s = pharmStrings
    val shown = state.pageItems.size
    val total = state.items.size
    val hasPrev = state.page > 1
    val hasNext = state.page < state.pageCount

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = s.movementsShownOf(shown, total),
            style = PharmText.micro.copy(color = t.colors.fg3),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.padding(end = 4.dp)) {
                Text(
                    text = s.movementsPagination(state.page, state.pageCount),
                    style = PharmText.micro.copy(color = t.colors.fg3),
                )
            }
            PharmButton(
                label = s.movementsPrevPage,
                onClick = callbacks.onPrevPage,
                size = PharmButtonSize.Sm,
                variant = PharmButtonVariant.Outline,
                enabled = hasPrev,
            )
            PharmButton(
                label = s.movementsNextPage,
                onClick = callbacks.onNextPage,
                size = PharmButtonSize.Sm,
                variant = PharmButtonVariant.Outline,
                enabled = hasNext,
            )
        }
    }
}
