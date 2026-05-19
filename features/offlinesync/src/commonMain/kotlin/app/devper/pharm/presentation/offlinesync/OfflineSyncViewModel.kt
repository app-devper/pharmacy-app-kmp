package app.devper.pharm.presentation.offlinesync

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.observer.OfflineQueueProvider
import app.devper.pharm.domain.usecase.MarkOfflineSaleSyncedUseCase
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class OfflineSyncViewModel(
    offlineQueue: OfflineQueueProvider,
    private val markSynced: MarkOfflineSaleSyncedUseCase,
) : BaseViewModel<OfflineSyncUiState>(OfflineSyncUiState()) {

    init {
        offlineQueue.pending
            .onEach { pending -> setState { copy(pending = pending.sortedBy { it.enqueuedAt }) } }
            .launchIn(viewModelScope)
    }

    fun refresh() = setState { copy(message = "ดึงสถานะคิวล่าสุดแล้ว") }

    fun syncAll() {
        if (current.pending.isEmpty()) return
        setState { copy(message = "เริ่มลองส่งบิลค้างทั้งหมดแล้ว") }
    }

    fun retry(id: String) {
        if (current.pending.none { it.id == id }) return
        setState { copy(message = "เริ่มลองส่งบิล ${id.take(8)} ใหม่แล้ว") }
    }

    fun askDiscard(id: String) = setState { copy(confirmDiscardId = id) }
    fun cancelDiscard() = setState { copy(confirmDiscardId = null) }

    fun discardConfirmed() {
        val id = current.confirmDiscardId ?: return
        markSynced(id)
        setState { copy(confirmDiscardId = null, message = "ลบรายการค้างซิงก์แล้ว") }
    }

    fun dismissMessage() = setState { copy(message = null) }
    fun dismissError() = setState { copy(error = null) }
}
