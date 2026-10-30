package app.devper.pharm.presentation.offlinesync

import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.model.PendingSaleState
import app.devper.pharm.presentation.offlinesync.exception.OfflineSyncUiStateError
import app.devper.pharm.presentation.offlinesync.message.OfflineSyncUiStateMessage
import app.devper.pharm.ui.common.BaseUiState
import kotlinx.datetime.TimeZone

data class OfflineSyncUiState(
    val tz: TimeZone = TimeZone.of("Asia/Bangkok"),
    val pending: List<PendingSale> = emptyList(),
    val syncingIds: Set<String> = emptySet(),
    val syncingAll: Boolean = false,
    val resolving: Resolving? = null,
    val working: Boolean = false,
    val exportedIds: Set<String> = emptySet(),
    val messageState: OfflineSyncUiStateMessage? = null,
    val errorState: OfflineSyncUiStateError? = null,
) : BaseUiState {

    override val loading: Boolean get() = syncingAll
    override val domainError: OfflineSyncUiStateError? get() = errorState
    val totalCount: Int get() = pending.size
    val retryableCount: Int get() = pending.count { it.state == PendingSaleState.Pending }
    val needsActionCount: Int get() = pending.count { it.state != PendingSaleState.Pending }
    val busy: Boolean get() = syncingAll || syncingIds.isNotEmpty() || working
}

sealed interface Resolving {
    val id: String

    data class Abandon(override val id: String, val kyOnly: Boolean, val reason: String = "") : Resolving

    data class DiscardDamaged(override val id: String) : Resolving
}
