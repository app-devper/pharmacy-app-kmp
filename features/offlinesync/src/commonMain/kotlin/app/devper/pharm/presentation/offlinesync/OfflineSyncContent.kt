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
import app.devper.pharm.domain.model.PendingSaleState
import app.devper.pharm.ui.components.LocalRolePermissions
import app.devper.pharm.ui.designsystem.PharmListBody
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.presentation.offlinesync.i18n.localize
import app.devper.pharm.presentation.offlinesync.message.OfflineSyncUiStateMessage
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.components.unlessPageShowsError
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListScaffold
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
    val pageIsEmpty = state.pending.isEmpty()
    val t = pharmTokens
    val s = pharmStrings

    PharmListScaffold(
        toolbar = {
            PharmListToolbar(
                subtitle = s.offlineSyncSubtitle,
                primaryAction = {
                    PharmButton(
                        label = s.offlineSyncRetryAllCta,
                        onClick = callbacks.onSyncAll,
                        variant = PharmButtonVariant.Primary,
                        size = PharmButtonSize.Sm,
                        enabled = state.retryableCount > 0 && !state.busy,
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
        PharmListBody(
            loading = state.loading,
            error = state.errorState,
            isEmpty = pageIsEmpty,
            onRetry = null,
            empty = { EmptyOfflineSync() },
            content = {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.pending, key = { it.id }) { row ->
                        OfflineSyncCard(
                            row = row,
                            tz = state.tz,
                            syncing = row.id in state.syncingIds || (state.syncingAll && row.state == PendingSaleState.Pending),
                            actionsEnabled = !state.busy,
                            canResolve = LocalRolePermissions.current.canResolvePendingSales,
                            exported = row.id in state.exportedIds,
                            callbacks = callbacks,
                        )
                    }
                }
            },
        )
    }

    state.resolving?.let { resolving -> ResolvingDialog(resolving, state.working, callbacks) }

    ErrorBottomSheet(message = state.errorState.unlessPageShowsError(pageIsEmpty)?.localize(pharmStrings), onDismiss = callbacks.onDismissError)
}

@Composable
private fun ResolvingDialog(resolving: Resolving, working: Boolean, callbacks: OfflineSyncCallbacks) {
    val s = pharmStrings
    val abandon = resolving as? Resolving.Abandon
    PharmModal(
        open = true,
        onDismiss = callbacks.onDismissResolving,
        title = when {
            abandon == null -> s.offlineSyncDeleteConfirmTitle
            abandon.kyOnly -> s.offlineSyncCloseKyTitle
            else -> s.offlineSyncAbandonTitle
        },
        footer = {
            PharmButton(
                label = s.commonCancel,
                onClick = callbacks.onDismissResolving,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Md,
                enabled = !working,
            )
            PharmButton(
                label = when {
                    abandon == null -> s.commonDelete
                    abandon.kyOnly -> s.offlineSyncCloseKyCta
                    else -> s.offlineSyncAbandonCta
                },
                onClick = callbacks.onConfirmResolving,
                variant = PharmButtonVariant.Danger,
                size = PharmButtonSize.Md,
                enabled = abandon == null || abandon.reason.isNotBlank(),
                loading = working,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = when {
                    abandon == null -> s.offlineSyncDeleteConfirmMessage
                    abandon.kyOnly -> s.offlineSyncCloseKyMessage
                    else -> s.offlineSyncAbandonMessage
                },
                style = PharmText.body,
            )
            if (abandon != null) {
                PharmTextField(
                    value = abandon.reason,
                    onValueChange = callbacks.onReasonChange,
                    placeholder = s.offlineSyncReasonPlaceholder,
                    enabled = !working,
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
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
        lastError = "received must be >= total",
        attempts = 3,
        state = PendingSaleState.Conflict,
    ),
    PendingSale(
        id = "3a870004",
        clientRequestId = "req-3a870004-dddd",
        payloadJson = "{}",
        enqueuedAt = 1716031000000L,
        lastError = "ky10:Tramadol:buyer_name is required",
        attempts = 1,
        state = PendingSaleState.KyPending,
        billNo = "INV-260517-004",
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
private fun OfflineSyncContent_Abandon_Preview() {
    PharmacyTheme {
        OfflineSyncContent(
            state = OfflineSyncUiState(
                pending = samplePending,
                resolving = Resolving.Abandon(samplePending[2].id, kyOnly = false, reason = "ลูกค้ายกเลิก"),
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
                messageState = OfflineSyncUiStateMessage.Abandoned,
            ),
        )
    }
}
