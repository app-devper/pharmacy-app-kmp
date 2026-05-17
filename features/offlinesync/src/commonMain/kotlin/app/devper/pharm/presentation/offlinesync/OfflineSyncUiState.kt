package app.devper.pharm.presentation.offlinesync

import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.ui.common.BaseUiState

data class OfflineSyncUiState(
    val pending: List<PendingSale> = emptyList(),
    val confirmDiscardId: String? = null,
    val message: String? = null,
    override val error: String? = null,
) : BaseUiState {

    override val loading: Boolean get() = false
    val totalCount: Int get() = pending.size
    val failedCount: Int get() = pending.count { it.lastError != null }
}
