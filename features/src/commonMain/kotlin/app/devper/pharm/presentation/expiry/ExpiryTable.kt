package app.devper.pharm.presentation.expiry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
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
    val headerState = when {
        selected.isEmpty() -> ToggleableState.Off
        allSelected -> ToggleableState.On
        else -> ToggleableState.Indeterminate
    }

    val columns = remember(callbacks, selected, t) {
        listOf(
        PharmTableColumn<ExpiringLot>(
            header = "",
            weight = 0.4f,
            cell = { lot ->
                Checkbox(
                    checked = lot.id in selected,
                    onCheckedChange = { callbacks.onToggleRow(lot.id) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = t.colors.accent,
                        uncheckedColor = t.colors.border,
                    ),
                )
            },
        ),
        PharmTableColumn(
            header = "ชื่อยา",
            weight = 2.4f,
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
            header = "เลขล็อต",
            weight = 1.2f,
            cell = { lot ->
                Text(
                    text = lot.lotNumber,
                    style = PharmText.micro.copy(
                        color = t.colors.fg3,
                        fontFamily = FontFamily.Monospace,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = "วันหมดอายุ",
            weight = 1.2f,
            cell = { lot -> ExpiryDateCell(lot) },
        ),
        PharmTableColumn(
            header = "คงเหลือ",
            weight = 0.8f,
            align = PharmColumnAlign.End,
            cell = { lot -> RemainingCell(lot) },
        ),
        PharmTableColumn(
            header = "สถานะ",
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
            rowHeight = 56.dp,
            emptyContent = {
                Text(
                    text = "ไม่มีล็อตในช่วงเวลานี้",
                    style = PharmText.meta,
                )
            },
            bottomRow = if (lots.isNotEmpty()) ({ SelectAllRow(headerState, callbacks) }) else null,
        )
    }
}

@Composable
private fun SelectAllRow(state: ToggleableState, callbacks: ExpiryCallbacks) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(t.colors.bgPage)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TriStateCheckbox(
            state = state,
            onClick = callbacks.onToggleAll,
            colors = CheckboxDefaults.colors(
                checkedColor = t.colors.accent,
                uncheckedColor = t.colors.border,
            ),
        )
        Text(
            text = when (state) {
                ToggleableState.On -> "เลือกทั้งหมด"
                ToggleableState.Indeterminate -> "เลือกบางส่วน · กดเพื่อล้าง"
                ToggleableState.Off -> "เลือกทั้งหมด"
            },
            style = PharmText.micro.copy(color = t.colors.fg3),
        )
    }
}

@Composable
private fun ExpiryDateCell(lot: ExpiringLot) {
    val t = pharmTokens
    Text(
        text = lot.expiryDate.take(10),
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
    val (tone, label) = expiryBadge(daysLeft)
    PharmBadge(text = label, tone = tone, size = PharmBadgeSize.Sm)
}

internal fun expiryBadge(daysLeft: Int): Pair<PharmBadgeTone, String> = when {
    daysLeft < 0    -> PharmBadgeTone.Red    to "หมดอายุแล้ว"
    daysLeft <= 30  -> PharmBadgeTone.Red    to "อีก $daysLeft วัน"
    daysLeft <= 60  -> PharmBadgeTone.Orange to "อีก $daysLeft วัน"
    daysLeft <= 90  -> PharmBadgeTone.Amber  to "อีก $daysLeft วัน"
    else            -> PharmBadgeTone.Blue   to "อีก $daysLeft วัน"
}
