package app.devper.pharm.presentation.offlinesync

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmActionTone
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListCard
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

@Composable
internal fun OfflineSyncCard(
    row: PendingSale,
    tz: TimeZone,
    callbacks: OfflineSyncCallbacks,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    PharmListCard(
        title = "OFFLINE-${row.id.take(8)}",
        subtitle = formatEnqueuedAt(row.enqueuedAt, tz),
        modifier = modifier,
        status = {
            if (row.lastError != null) {
                PharmStatusBadge(status = PharmStatus.Failed, label = "ล้มเหลว", size = PharmBadgeSize.Sm)
            } else {
                PharmStatusBadge(status = PharmStatus.Pending, label = "รอซิงก์", size = PharmBadgeSize.Sm)
                if (row.attempts > 0) {
                    Text(
                        text = "ลอง ${row.attempts} ครั้ง",
                        style = PharmText.micro.copy(color = t.colors.fgMuted),
                    )
                }
            }
        },
        body = {
            if (row.lastError != null) {
                Text(
                    text = row.lastError ?: "",
                    style = PharmText.micro.copy(color = t.colors.dangerFg),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        trailing = {
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
        },
    )
}

@OptIn(ExperimentalTime::class)
private fun formatEnqueuedAt(millis: Long, tz: TimeZone): String {
    val dt = Instant.fromEpochMilliseconds(millis).toLocalDateTime(tz)
    val mm = dt.month.number.toString().padStart(2, '0')
    val dd = dt.day.toString().padStart(2, '0')
    val yy = (dt.year % 100).toString().padStart(2, '0')
    val hh = dt.hour.toString().padStart(2, '0')
    val mi = dt.minute.toString().padStart(2, '0')
    return "$dd/$mm/$yy $hh:$mi"
}
