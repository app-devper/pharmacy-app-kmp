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
            .onEach { pending -> setState { copy(pending = pending.sortedBy { it.enqueuedAt }) } }
            .catch { e -> setState { copy(errorState = OfflineSyncUiStateError.LoadFailed(e)) } }
            .launchIn(viewModelScope)
    }

    fun refresh() = setState { copy(messageState = OfflineSyncUiStateMessage.Refreshed) }

    fun syncAll() {
        val snapshot = current.pending
        if (snapshot.isEmpty()) return
        setState { copy(messageState = OfflineSyncUiStateMessage.SyncStarted(snapshot.size)) }
        viewModelScope.launch {
            var failed = 0
            snapshot.forEach { item ->
                retrySale(item.id).onFailure { failed++ }
            }
            if (failed > 0) {
                setState { copy(errorState = OfflineSyncUiStateError.SyncPartialFailed(failed, snapshot.size)) }
            }
        }
    }

    fun retry(id: String) {
        if (current.pending.none { it.id == id }) return
        setState { copy(messageState = OfflineSyncUiStateMessage.RetryStarted(id.take(8))) }
        retryOne(id)
    }

    private fun retryOne(id: String) {
        launchResult(
            block = { retrySale(id) },
            onSuccess = { },
            onFailure = { e ->
                setState { copy(errorState = OfflineSyncUiStateError.RetryFailed(id.take(8), e)) }
            },
        )
    }

    fun askDiscard(id: String) = setState { copy(confirmDiscardId = id) }
    fun cancelDiscard() = setState { copy(confirmDiscardId = null) }

    fun discardConfirmed() {
        val id = current.confirmDiscardId ?: return
        launchResult(
            block = { markSynced(id) },
            onSuccess = { setState { copy(confirmDiscardId = null, messageState = OfflineSyncUiStateMessage.Discarded) } },
            onFailure = { e -> setState { copy(errorState = OfflineSyncUiStateError.DiscardFailed(e)) } },
        )
    }

    fun dismissMessage() = setState { copy(messageState = null) }
    fun dismissError() = setState { copy(errorState = null) }
}
