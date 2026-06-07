package app.devper.pharm.presentation.offlinesync

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.observer.OfflineQueueProvider
import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.usecase.MarkOfflineSaleSyncedUseCase
import app.devper.pharm.domain.usecase.RetryOfflineSaleUseCase
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
            .catch { e -> setState { copy(error = e.message ?: "โหลดรายการค้างซิงก์ไม่สำเร็จ") } }
            .launchIn(viewModelScope)
    }

    fun refresh() = setState { copy(message = "ดึงสถานะคิวล่าสุดแล้ว") }

    fun syncAll() {
        val snapshot = current.pending
        if (snapshot.isEmpty()) return
        setState { copy(message = "เริ่มซิงก์ ${snapshot.size} รายการ") }
        viewModelScope.launch {
            var failed = 0
            snapshot.forEach { item ->
                retrySale(item.id).onFailure { failed++ }
            }
            if (failed > 0) setState { copy(error = "ส่งบิลไม่สำเร็จ $failed จาก ${snapshot.size} รายการ") }
        }
    }

    fun retry(id: String) {
        if (current.pending.none { it.id == id }) return
        setState { copy(message = "เริ่มลองส่งบิล ${id.take(8)} ใหม่แล้ว") }
        retryOne(id)
    }

    private fun retryOne(id: String) {
        launchResult(
            block = { retrySale(id) },
            onSuccess = { },
            onFailure = { e ->
                setState { copy(error = "ส่งบิล ${id.take(8)} ไม่สำเร็จ: ${e.message ?: "ไม่ทราบสาเหตุ"}") }
            },
        )
    }

    fun askDiscard(id: String) = setState { copy(confirmDiscardId = id) }
    fun cancelDiscard() = setState { copy(confirmDiscardId = null) }

    fun discardConfirmed() {
        val id = current.confirmDiscardId ?: return
        launchResult(
            block = { markSynced(id) },
            onSuccess = { setState { copy(confirmDiscardId = null, message = "ลบรายการค้างซิงก์แล้ว") } },
            onFailure = { e -> setState { copy(error = "ลบรายการไม่สำเร็จ: ${e.message ?: "ไม่ทราบสาเหตุ"}") } },
        )
    }

    fun dismissMessage() = setState { copy(message = null) }
    fun dismissError() = setState { copy(error = null) }
}
