package app.devper.pharm.presentation.offlinesync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.presentation.offlinesync.i18n.localize
import app.devper.pharm.presentation.offlinesync.message.OfflineSyncUiStateMessage
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmErrorState
import app.devper.pharm.ui.designsystem.unlessPageShowsError
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListScaffold
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OfflineSyncContent(
    state: OfflineSyncUiState,
    callbacks: OfflineSyncCallbacks = OfflineSyncCallbacks(),
) {
    val t = pharmTokens
    val s = pharmStrings

    PharmListScaffold(
        toolbar = {
            PharmListToolbar(
                subtitle = s.offlineSyncSubtitle,
                compactTopbarActions = true,
                actions = {
                    PharmButton(
                        label = s.offlineSyncRetryAllCta,
                        onClick = callbacks.onSyncAll,
                        variant = PharmButtonVariant.Primary,
                        size = PharmButtonSize.Sm,
                        enabled = state.totalCount > 0,
                        loading = state.syncingAll,
                        leadingIcon = {
                            Icon(
                                imageVector = PharmIcons.OfflineSync,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        },
                    )
                },
            )
        },
        metrics = { OfflineSyncMetricsRow(pending = state.pending) },
        resultLine = {
            PharmListResultLine(
                total = state.pending.size,
                noun = s.movementsCountNoun,
            )
        },
    ) {
        when {
            state.loading && state.pending.isEmpty() -> PharmListSkeleton()
            state.errorState != null && state.pending.isEmpty() ->
                PharmErrorState()
            state.pending.isEmpty() -> EmptyOfflineSync()
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.pending, key = { it.id }) { row ->
                    OfflineSyncCard(
                        row = row,
                        tz = state.tz,
                        syncing = row.id in state.syncingIds,
                        actionsEnabled = !state.busy,
                        callbacks = callbacks,
                    )
                }
            }
        }
    }

    state.confirmDiscardId?.let {
        PharmModal(
            open = true,
            onDismiss = callbacks.onDismissCancel,
            title = s.offlineSyncDeleteConfirmTitle,
            footer = {
                PharmButton(
                    label = s.commonCancel,
                    onClick = callbacks.onDismissCancel,
                    variant = PharmButtonVariant.Ghost,
                    size = PharmButtonSize.Md,
                    enabled = !state.discarding,
                )
                PharmButton(
                    label = s.commonDelete,
                    onClick = callbacks.onConfirmCancel,
                    variant = PharmButtonVariant.Danger,
                    size = PharmButtonSize.Md,
                    loading = state.discarding,
                )
            },
        ) {
            Text(
                text = s.offlineSyncDeleteConfirmMessage,
                style = PharmText.body,
            )
        }
    }

    ErrorBottomSheet(message = state.errorState.unlessPageShowsError(state.pending.isEmpty())?.localize(pharmStrings), onDismiss = callbacks.onDismissError)
}

@Composable
private fun EmptyOfflineSync() {
    val s = pharmStrings
    PharmEmptyState(
        icon = PharmIcons.Check,
        title = s.offlineSyncEmptyTitle,
        subtitle = s.offlineSyncEmpty,
    )
}

private val samplePending = listOf(
    PendingSale(
        id = "3a8f0001",
        clientRequestId = "req-3a8f0001-aaaa",
        payloadJson = "{}",
        enqueuedAt = 1716040920000L,
        lastError = null,
        attempts = 0,
    ),
    PendingSale(
        id = "3a8c0002",
        clientRequestId = "req-3a8c0002-bbbb",
        payloadJson = "{}",
        enqueuedAt = 1716035880000L,
        lastError = null,
        attempts = 1,
    ),
    PendingSale(
        id = "3a890003",
        clientRequestId = "req-3a890003-cccc",
        payloadJson = "{}",
        enqueuedAt = 1716033000000L,
        lastError = "lot_mismatch: PCM-25011 ถูกตัดหมด — backend retried with FEFO",
        attempts = 3,
    ),
)

@Preview
@Composable
private fun OfflineSyncContent_Loaded_Preview() {
    PharmacyTheme {
        OfflineSyncContent(state = OfflineSyncUiState(pending = samplePending))
    }
}

@Preview
@Composable
private fun OfflineSyncContent_Empty_Preview() {
    PharmacyTheme {
        OfflineSyncContent(state = OfflineSyncUiState(pending = emptyList()))
    }
}

@Preview
@Composable
private fun OfflineSyncContent_ConfirmDiscard_Preview() {
    PharmacyTheme {
        OfflineSyncContent(
            state = OfflineSyncUiState(
                pending = samplePending,
                confirmDiscardId = samplePending.first().id,
            ),
        )
    }
}

@Preview
@Composable
private fun OfflineSyncContent_WithFailures_Preview() {
    PharmacyTheme {
        OfflineSyncContent(
            state = OfflineSyncUiState(
                pending = samplePending,
                messageState = OfflineSyncUiStateMessage.Discarded,
            ),
        )
    }
}
