package app.devper.pharm.presentation.offlinesync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmActionTone
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

@Composable
internal fun OfflineSyncTable(
    pending: List<PendingSale>,
    callbacks: OfflineSyncCallbacks,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val columns = remember(callbacks, t) {
        listOf(
        PharmTableColumn<PendingSale>(
            header = "เวลา",
            weight = 1.2f,
            cell = { row ->
                Text(
                    text = formatEnqueuedAt(row.enqueuedAt),
                    style = PharmText.micro.copy(
                        color = t.colors.fg3,
                        fontFamily = FontFamily.Monospace,
                    ),
                )
            },
        ),
        PharmTableColumn(
            header = "รหัส queue",
            weight = 1.5f,
            cell = { row ->
                Text(
                    text = "OFFLINE-${row.id.take(8)}",
                    style = PharmText.micro.copy(
                        color = t.colors.fg1,
                        fontFamily = FontFamily.Monospace,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = "รายการ",
            weight = 0.7f,
            align = PharmColumnAlign.End,
            cell = {
                Text(
                    text = "—",
                    style = PharmText.bodySm.copy(color = t.colors.fg2),
                )
            },
        ),
        PharmTableColumn(
            header = "ยอดรวม",
            weight = 1.0f,
            align = PharmColumnAlign.End,
            cell = {
                Text(
                    text = "—",
                    style = PharmText.bodySm.copy(
                        color = t.colors.fg1,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            },
        ),
        PharmTableColumn(
            header = "สถานะ",
            weight = 1.6f,
            cell = { row -> OfflineSyncStatusCell(row) },
        ),
        PharmTableColumn(
            header = "จัดการ",
            weight = 0.6f,
            align = PharmColumnAlign.End,
            cell = { row -> OfflineSyncRowActions(row = row, callbacks = callbacks) },
        ),
        )
    }

    PharmTable(
        rows = pending,
        columns = columns,
        key = { it.id },
        modifier = modifier,
        rowHeight = 68.dp,
        emptyContent = {
            Text(text = "ไม่มีบิลค้างซิงก์", style = PharmText.meta)
        },
    )
}

@Composable
private fun OfflineSyncStatusCell(row: PendingSale) {
    val t = pharmTokens
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        if (row.lastError != null) {
            PharmStatusBadge(status = PharmStatus.Failed, label = "ล้มเหลว", size = PharmBadgeSize.Sm)
            Text(
                text = row.lastError ?: "",
                style = PharmText.micro.copy(color = t.colors.dangerFg),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        } else {
            PharmStatusBadge(status = PharmStatus.Pending, label = "รอซิงก์", size = PharmBadgeSize.Sm)
            if (row.attempts > 0) {
                Text(
                    text = "ลอง ${row.attempts} ครั้ง — รอเชื่อมต่อใหม่",
                    style = PharmText.micro.copy(color = t.colors.fgMuted),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun OfflineSyncRowActions(row: PendingSale, callbacks: OfflineSyncCallbacks) {
    PharmActionMenu(
        actions = listOf(
            PharmAction(
                label = "ลองส่งใหม่",
                icon = PharmIcons.OfflineSync,
                tone = PharmActionTone.Primary,
                onClick = { callbacks.onRetry(row) },
            ),
            PharmAction(
                label = "ยกเลิก",
                icon = PharmIcons.Ban,
                tone = PharmActionTone.Danger,
                onClick = { callbacks.onCancel(row) },
            ),
        ),
    )
}

@OptIn(ExperimentalTime::class)
private fun formatEnqueuedAt(millis: Long): String {
    val dt = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault())
    val mm = dt.month.number.toString().padStart(2, '0')
    val dd = dt.day.toString().padStart(2, '0')
    val yy = (dt.year % 100).toString().padStart(2, '0')
    val hh = dt.hour.toString().padStart(2, '0')
    val mi = dt.minute.toString().padStart(2, '0')
    return "$dd/$mm/$yy $hh:$mi"
}
