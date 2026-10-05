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
    /** The abandon, close-KY, or discard dialog, when open. */
    val resolving: Resolving? = null,
    val working: Boolean = false,
    /** Damaged entries exported in this session; only those can be discarded. */
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

/** A decision a person is making about one entry. */
sealed interface Resolving {
    val id: String

    /** Abandon a sale, or close the refused KY forms of a recorded one, with [reason]. */
    data class Abandon(override val id: String, val kyOnly: Boolean, val reason: String = "") : Resolving

    data class DiscardDamaged(override val id: String) : Resolving
}
