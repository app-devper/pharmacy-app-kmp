package app.devper.pharm.presentation.expiry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmCheckbox
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmStamp
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.designsystem.PharmTriStateCheckbox
import app.devper.pharm.ui.format.localDateToBuddhist
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun ExpiryTable(
    lots: List<ExpiringLot>,
    selected: Set<String>,
    allSelected: Boolean,
    callbacks: ExpiryCallbacks,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val s = pharmStrings
    val headerState = when {
        selected.isEmpty() -> ToggleableState.Off
        allSelected -> ToggleableState.On
        else -> ToggleableState.Indeterminate
    }

    val columns = remember(callbacks, selected, t, s) {
        listOf(
        PharmTableColumn<ExpiringLot>(
            header = s.commonPick,
            weight = 0.4f,
            cell = { lot ->
                PharmCheckbox(
                    checked = lot.id in selected,
                    onCheckedChange = { callbacks.onToggleRow(lot.id) },
                    contentDescription = lot.drugName,
                )
            },
        ),
        PharmTableColumn(
            header = s.expiryHeaderDrugName,
            weight = 2.4f,
            compactTitle = true,
            cell = { lot ->
                Text(
                    text = lot.drugName,
                    style = PharmText.bodySm.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = s.expiryHeaderLotNumber,
            weight = 1.2f,
            hideInCompact = true,
            cell = { lot ->
                PharmStamp(text = lot.lotNumber)
            },
        ),
        PharmTableColumn(
            header = s.expiryHeaderExpiry,
            weight = 1.2f,
            cell = { lot -> ExpiryDateCell(lot) },
        ),
        PharmTableColumn(
            header = s.expiryHeaderRemaining,
            weight = 0.8f,
            align = PharmColumnAlign.End,
            cell = { lot -> RemainingCell(lot) },
        ),
        PharmTableColumn(
            header = s.commonStatus,
            weight = 1.2f,
            cell = { lot -> ExpiryStatusBadge(lot.daysLeft) },
        ),
        )
    }

    Box(modifier = modifier) {
        PharmTable(
            rows = lots,
            columns = columns,
            key = { it.id },
            onRowClick = { lot -> callbacks.onToggleRow(lot.id) },
            emptyContent = {
                PharmEmptyState(
                    icon = PharmIcons.Expiry,
                    title = s.expiryEmpty,
                )
            },
            bottomRow = if (lots.isNotEmpty()) ({ SelectAllRow(headerState, callbacks) }) else null,
        )
    }
}

@Composable
private fun SelectAllRow(state: ToggleableState, callbacks: ExpiryCallbacks) {
    val t = pharmTokens
    val s = pharmStrings
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(t.colors.bgPage)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PharmTriStateCheckbox(
            state = state,
            onClick = callbacks.onToggleAll,
            contentDescription = s.expirySelectAll,
        )
        Text(
            text = when (state) {
                ToggleableState.On -> s.expirySelectAll
                ToggleableState.Indeterminate -> s.expirySelectPartial
                ToggleableState.Off -> s.expirySelectAll
            },
            style = PharmText.micro.copy(color = t.colors.fg3),
        )
    }
}

@Composable
private fun ExpiryDateCell(lot: ExpiringLot) {
    val t = pharmTokens
    Text(
        text = localDateToBuddhist(lot.expiryDate),
        style = PharmText.bodySm.copy(color = t.colors.fg2).copy(
            fontFeatureSettings = "tnum",
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun RemainingCell(lot: ExpiringLot) {
    val t = pharmTokens
    Text(
        text = lot.remaining.toString(),
        style = PharmText.bodySm.copy(
            color = t.colors.fg2,
            fontFeatureSettings = "tnum",
        ),
    )
}

@Composable
private fun ExpiryStatusBadge(daysLeft: Int) {
    val s = pharmStrings
    val tone = expiryBadgeTone(daysLeft)
    val label = if (daysLeft < 0) s.expiryStatusExpired else s.expiryStatusDaysLeft(daysLeft)
    PharmBadge(text = label, tone = tone, size = PharmBadgeSize.Sm)
}

internal fun expiryBadgeTone(daysLeft: Int): PharmBadgeTone = when {
    daysLeft < 0    -> PharmBadgeTone.Red
    daysLeft <= 30  -> PharmBadgeTone.Red
    daysLeft <= 60  -> PharmBadgeTone.Orange
    daysLeft <= 90  -> PharmBadgeTone.Amber
    else            -> PharmBadgeTone.Blue
}
