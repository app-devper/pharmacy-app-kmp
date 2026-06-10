package app.devper.pharm.presentation.expiry

import app.devper.pharm.common.AppException
import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.model.WriteoffResult
import app.devper.pharm.ui.common.LoadableUiState

enum class ExpiryWindow(val label: String, val daysAhead: Int?, val expiredOnly: Boolean) {
    Within30("30 วัน", 30, false),
    Within60("60 วัน", 60, false),
    Within90("90 วัน", 90, false),
    Within180("180 วัน", 180, false),
    ExpiredOnly("หมดอายุแล้ว", null, true),
}

data class ExpiryUiState(
    val window: ExpiryWindow = ExpiryWindow.Within60,
    override val loading: Boolean = false,
    val lots: List<ExpiringLot> = emptyList(),
    val selected: Set<String> = emptySet(),
    val confirmDialog: Boolean = false,
    val writingOff: Boolean = false,
    val writeoffResult: WriteoffResult? = null,
    val errorState: AppException? = null,
) : LoadableUiState<ExpiryUiState> {

    override val domainError: AppException? get() = errorState
    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val canWriteoff: Boolean get() = !writingOff && selected.isNotEmpty()
    val totalSelected: Int get() = selected.size
    val totalRemaining: Int get() = lots.sumOf { it.remaining }
    val allSelected: Boolean get() = lots.isNotEmpty() && selected.size == lots.size
}
