package app.devper.pharm.presentation.offlinesync

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.model.PendingSaleState
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
    syncing: Boolean,
    actionsEnabled: Boolean,
    canResolve: Boolean,
    exported: Boolean,
    callbacks: OfflineSyncCallbacks,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val s = pharmStrings
    PharmListCard(
        title = row.billNo ?: "OFFLINE-${row.id.take(8)}",
        subtitle = if (row.state == PendingSaleState.Damaged) row.id else millisToBuddhistDisplayWithTime(row.enqueuedAt, tz),
        modifier = modifier,
        status = {
            when {
                syncing ->
                    PharmStatusBadge(status = PharmStatus.Pending, label = s.offlineSyncStatusSyncing, size = PharmBadgeSize.Sm)
                row.state == PendingSaleState.Conflict ->
                    PharmStatusBadge(status = PharmStatus.Failed, label = s.offlineSyncStatusConflict, size = PharmBadgeSize.Sm)
                row.state == PendingSaleState.KyPending ->
                    PharmStatusBadge(status = PharmStatus.Failed, label = s.offlineSyncStatusKyPending, size = PharmBadgeSize.Sm)
                row.state == PendingSaleState.Damaged ->
                    PharmStatusBadge(status = PharmStatus.Failed, label = s.offlineSyncStatusDamaged, size = PharmBadgeSize.Sm)
                else -> {
                    PharmStatusBadge(status = PharmStatus.Pending, label = s.offlineSyncStatusPending, size = PharmBadgeSize.Sm)
                    if (row.attempts > 0) {
                        Text(
                            text = s.offlineSyncAttemptsLabel(row.attempts),
                            style = PharmText.micro.copy(color = t.colors.fgMuted),
                        )
                    }
                }
            }
        },
        body = {
            when (row.state) {
                PendingSaleState.KyPending -> Text(
                    text = s.offlineSyncKyPendingBill(row.billNo.orEmpty()),
                    style = PharmText.micro,
                )
                PendingSaleState.Damaged -> Text(
                    text = s.offlineSyncDamagedHint,
                    style = PharmText.micro,
                )
                else -> Unit
            }
            row.lastError?.takeIf { it.isNotBlank() }?.let { reason ->
                Text(
                    text = reason,
                    style = PharmText.micro.copy(color = t.colors.dangerFg),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        trailing = {
            PharmActionMenu(actions = actionsFor(row, actionsEnabled, canResolve, exported, callbacks))
        },
    )
}

@Composable
private fun actionsFor(
    row: PendingSale,
    enabled: Boolean,
    canResolve: Boolean,
    exported: Boolean,
    callbacks: OfflineSyncCallbacks,
): List<PharmAction> {
    val s = pharmStrings
    val export = PharmAction(
        label = s.offlineSyncExportCta,
        icon = PharmIcons.Imports,
        tone = PharmActionTone.Default,
        enabled = enabled,
        onClick = { callbacks.onExport(row) },
    )
    if (row.state == PendingSaleState.Damaged) {
        return listOfNotNull(
            export,
            PharmAction(
                label = s.commonDelete,
                icon = PharmIcons.Trash,
                tone = PharmActionTone.Danger,
                enabled = enabled && exported,
                onClick = { callbacks.onDiscard(row) },
            ).takeIf { canResolve },
        )
    }
    return listOfNotNull(
        PharmAction(
            label = s.offlineSyncRetryRowCta,
            icon = PharmIcons.OfflineSync,
            tone = PharmActionTone.Primary,
            enabled = enabled,
            onClick = { callbacks.onRetry(row) },
        ),
        PharmAction(
            label = if (row.state == PendingSaleState.KyPending) s.offlineSyncCloseKyCta else s.offlineSyncAbandonCta,
            icon = PharmIcons.Ban,
            tone = PharmActionTone.Danger,
            enabled = enabled,
            onClick = { callbacks.onAbandon(row) },
        ).takeIf { canResolve },
        export,
    )
}
