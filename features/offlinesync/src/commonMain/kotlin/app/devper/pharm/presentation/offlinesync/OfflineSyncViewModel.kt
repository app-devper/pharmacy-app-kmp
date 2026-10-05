package app.devper.pharm.presentation.offlinesync

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.model.PendingSaleState
import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.pendingsales.PendingSales
import app.devper.pharm.presentation.offlinesync.exception.OfflineSyncUiStateError
import app.devper.pharm.presentation.offlinesync.message.OfflineSyncUiStateMessage
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/** The pending sales screen; every decision is made by [PendingSales]. */
class OfflineSyncViewModel(
    private val pendingSales: PendingSales,
    timeZoneProvider: TimeZoneProvider,
) : BaseViewModel<OfflineSyncUiState>(OfflineSyncUiState(tz = timeZoneProvider.current)) {

    init {
        pendingSales.entries
            .onEach { entries ->
                setState {
                    copy(
                        pending = entries.sortedBy { it.enqueuedAt },
                        resolving = resolving?.takeIf { r -> entries.any { it.id == r.id } },
                    )
                }
            }
            .catch { e -> setState { copy(errorState = OfflineSyncUiStateError.LoadFailed(e)) } }
            .launchIn(viewModelScope)
    }

    /** Send every entry that is waiting only for the server. */
    fun syncAll() {
        val s = current
        val count = s.retryableCount
        if (count == 0 || s.busy) return
        setState {
            copy(syncingAll = true, errorState = null, messageState = OfflineSyncUiStateMessage.SyncStarted(count))
        }
        viewModelScope.launch {
            val summary = pendingSales.syncAll()
            val failed = summary.refused + summary.stillPending
            setState {
                copy(
                    syncingAll = false,
                    errorState = if (failed > 0) OfflineSyncUiStateError.SyncPartialFailed(failed, count) else null,
                )
            }
        }
    }

    fun retry(id: String) {
        val s = current
        val entry = s.pending.firstOrNull { it.id == id } ?: return
        if (s.busy || entry.state == PendingSaleState.Damaged) return
        setState {
            copy(
                syncingIds = syncingIds + id,
                errorState = null,
                messageState = OfflineSyncUiStateMessage.RetryStarted(id.take(8)),
            )
        }
        launchResult(
            block = { pendingSales.retry(id) },
            onSuccess = { state ->
                setState {
                    copy(
                        syncingIds = syncingIds - id,
                        messageState = if (state == null) OfflineSyncUiStateMessage.Recorded else messageState,
                        errorState = if (state == null) null else OfflineSyncUiStateError.StillNotRecorded(id.take(8)),
                    )
                }
            },
            onFailure = { e ->
                setState { copy(syncingIds = syncingIds - id, errorState = OfflineSyncUiStateError.RetryFailed(id.take(8), e)) }
            },
        )
    }

    /** Open the abandon dialog: a whole sale, or only the refused KY forms of a recorded one. */
    fun askAbandon(id: String) = setState {
        val entry = pending.firstOrNull { it.id == id }
        if (busy || entry == null || entry.state == PendingSaleState.Damaged) this
        else copy(resolving = Resolving.Abandon(id, kyOnly = entry.state == PendingSaleState.KyPending))
    }

    fun reasonChanged(reason: String) = setState {
        val r = resolving as? Resolving.Abandon ?: return@setState this
        copy(resolving = r.copy(reason = reason))
    }

    fun askDiscard(id: String) = setState {
        if (!busy && id in exportedIds && pending.any { it.id == id && it.state == PendingSaleState.Damaged }) {
            copy(resolving = Resolving.DiscardDamaged(id))
        } else this
    }

    fun cancelResolving() = setState { if (working) this else copy(resolving = null) }

    fun confirmResolving() {
        val s = current
        val r = s.resolving ?: return
        if (s.busy) return
        when (r) {
            is Resolving.Abandon -> {
                if (r.reason.isBlank()) return
                setState { copy(working = true, errorState = null) }
                launchResult(
                    block = { pendingSales.abandon(r.id, r.reason) },
                    onSuccess = {
                        setState { copy(working = false, resolving = null, messageState = OfflineSyncUiStateMessage.Abandoned) }
                    },
                    onFailure = { e ->
                        setState { copy(working = false, errorState = OfflineSyncUiStateError.AbandonFailed(e)) }
                    },
                )
            }
            is Resolving.DiscardDamaged -> {
                pendingSales.discardDamaged(r.id).fold(
                    onSuccess = {
                        setState { copy(resolving = null, messageState = OfflineSyncUiStateMessage.Discarded) }
                    },
                    onFailure = { e -> setState { copy(errorState = OfflineSyncUiStateError.DiscardFailed(e)) } },
                )
            }
        }
    }

    fun export(id: String) {
        if (current.busy) return
        setState { copy(working = true, errorState = null) }
        launchResult(
            block = { pendingSales.export(id) },
            onSuccess = { path ->
                setState {
                    copy(
                        working = false,
                        exportedIds = exportedIds + id,
                        messageState = OfflineSyncUiStateMessage.Exported(path),
                    )
                }
            },
            onFailure = { e -> setState { copy(working = false, errorState = OfflineSyncUiStateError.ExportFailed(e)) } },
        )
    }

    fun dismissMessage() = setState { copy(messageState = null) }
    fun dismissError() = setState { copy(errorState = null) }
}
