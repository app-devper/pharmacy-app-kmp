package app.devper.pharm.presentation.offlinesync

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.observer.OfflineQueueProvider
import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.usecase.offlinesync.MarkOfflineSaleSyncedUseCase
import app.devper.pharm.domain.usecase.offlinesync.RetryOfflineSaleUseCase
import app.devper.pharm.presentation.offlinesync.exception.OfflineSyncUiStateError
import app.devper.pharm.presentation.offlinesync.message.OfflineSyncUiStateMessage
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class OfflineSyncViewModel(
    offlineQueue: OfflineQueueProvider,
    private val markSynced: MarkOfflineSaleSyncedUseCase,
    private val retrySale: RetryOfflineSaleUseCase,
    timeZoneProvider: TimeZoneProvider,
) : BaseViewModel<OfflineSyncUiState>(OfflineSyncUiState(tz = timeZoneProvider.current)) {

    init {
        offlineQueue.pending
            .onEach { pending ->
                setState {
                    copy(
                        pending = pending.sortedBy { it.enqueuedAt },
                        confirmDiscardId = confirmDiscardId?.takeIf { id -> pending.any { it.id == id } },
                    )
                }
            }
            .catch { e -> setState { copy(errorState = OfflineSyncUiStateError.LoadFailed(e)) } }
            .launchIn(viewModelScope)
    }

    fun syncAll() {
        val s = current
        val snapshot = s.pending
        if (snapshot.isEmpty() || s.busy) return
        val ids = snapshot.map { it.id }.toSet()
        setState {
            copy(
                syncingAll = true,
                syncingIds = ids,
                errorState = null,
                messageState = OfflineSyncUiStateMessage.SyncStarted(snapshot.size),
            )
        }
        viewModelScope.launch {
            var failed = 0
            snapshot.forEach { item ->
                retrySale(item.id).onFailure { failed++ }
            }
            setState {
                copy(
                    syncingAll = false,
                    syncingIds = emptySet(),
                    errorState = if (failed > 0) OfflineSyncUiStateError.SyncPartialFailed(failed, snapshot.size) else null,
                )
            }
        }
    }

    fun retry(id: String) {
        val s = current
        if (s.pending.none { it.id == id } || s.busy) return
        setState {
            copy(
                syncingIds = syncingIds + id,
                errorState = null,
                messageState = OfflineSyncUiStateMessage.RetryStarted(id.take(8)),
            )
        }
        retryOne(id)
    }

    private fun retryOne(id: String) {
        launchResult(
            block = { retrySale(id) },
            onSuccess = { setState { copy(syncingIds = syncingIds - id) } },
            onFailure = { e ->
                setState {
                    copy(
                        syncingIds = syncingIds - id,
                        errorState = OfflineSyncUiStateError.RetryFailed(id.take(8), e),
                    )
                }
            },
        )
    }

    fun askDiscard(id: String) = setState {
        if (!busy && pending.any { it.id == id }) copy(confirmDiscardId = id) else this
    }
    fun cancelDiscard() = setState { copy(confirmDiscardId = null) }

    fun discardConfirmed() {
        val s = current
        val id = s.confirmDiscardId ?: return
        if (s.busy || s.pending.none { it.id == id }) return
        setState { copy(discarding = true, errorState = null) }
        launchResult(
            block = { markSynced(id) },
            onSuccess = {
                setState {
                    copy(
                        discarding = false,
                        confirmDiscardId = null,
                        messageState = OfflineSyncUiStateMessage.Discarded,
                    )
                }
            },
            onFailure = { e ->
                setState { copy(discarding = false, errorState = OfflineSyncUiStateError.DiscardFailed(e)) }
            },
        )
    }

    fun dismissMessage() = setState { copy(messageState = null) }
    fun dismissError() = setState { copy(errorState = null) }
}
