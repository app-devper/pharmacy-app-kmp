package app.devper.pharm.presentation.expiry

import app.devper.pharm.common.AppException
import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.model.WriteoffResult
import app.devper.pharm.ui.common.LoadableUiState

enum class ExpiryWindow(val daysAhead: Int?, val expiredOnly: Boolean) {
    Within30(30, false),
    Within60(60, false),
    Within90(90, false),
    Within180(180, false),
    ExpiredOnly(null, true),
}

data class ExpiryUiState(
    val window: ExpiryWindow = ExpiryWindow.Within60,
    val query: String = "",
    override val loading: Boolean = false,
    val lots: List<ExpiringLot> = emptyList(),
    val selected: Set<String> = emptySet(),
    val confirmDialog: Boolean = false,
    val writingOff: Boolean = false,
    val writeoffResult: WriteoffResult? = null,
    val errorState: AppException? = null,
) : LoadableUiState<ExpiryUiState> {

    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val canWriteoff: Boolean get() = !writingOff && selected.isNotEmpty()
    val totalSelected: Int get() = selected.size
    val totalRemaining: Int get() = lots.sumOf { it.remaining }
    val filteredLots: List<ExpiringLot> by lazy {
        val needle = query.trim()
        if (needle.isEmpty()) lots
        else lots.filter { lot ->
            lot.drugName.contains(needle, ignoreCase = true) ||
                lot.lotNumber.contains(needle, ignoreCase = true)
        }
    }
    val filteredRemaining: Int get() = filteredLots.sumOf { it.remaining }
    val allVisibleSelected: Boolean
        get() = filteredLots.isNotEmpty() && filteredLots.all { it.id in selected }
}
