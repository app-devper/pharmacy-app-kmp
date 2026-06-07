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
import app.devper.pharm.ui.format.millisToBuddhistDisplayWithTime
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import kotlinx.datetime.TimeZone

@Composable
internal fun OfflineSyncCard(
    row: PendingSale,
    tz: TimeZone,
    callbacks: OfflineSyncCallbacks,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val s = pharmStrings
    PharmListCard(
        title = "OFFLINE-${row.id.take(8)}",
        subtitle = formatEnqueuedAt(row.enqueuedAt, tz),
        modifier = modifier,
        status = {
            if (row.lastError != null) {
                PharmStatusBadge(status = PharmStatus.Failed, label = s.offlineSyncStatusFailed, size = PharmBadgeSize.Sm)
            } else {
                PharmStatusBadge(status = PharmStatus.Pending, label = s.offlineSyncStatusPending, size = PharmBadgeSize.Sm)
                if (row.attempts > 0) {
                    Text(
                        text = s.offlineSyncAttemptsLabel(row.attempts),
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
                        label = s.offlineSyncRetryRowCta,
                        icon = PharmIcons.OfflineSync,
                        tone = PharmActionTone.Primary,
                        onClick = { callbacks.onRetry(row) },
                    ),
                    PharmAction(
                        label = s.commonCancel,
                        icon = PharmIcons.Ban,
                        tone = PharmActionTone.Danger,
                        onClick = { callbacks.onCancel(row) },
                    ),
                ),
            )
        },
    )
}

private fun formatEnqueuedAt(millis: Long, tz: TimeZone): String =
    millisToBuddhistDisplayWithTime(millis, tz)
