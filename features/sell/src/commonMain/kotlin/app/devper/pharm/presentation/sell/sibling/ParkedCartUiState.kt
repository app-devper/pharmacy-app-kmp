package app.devper.pharm.presentation.sell.sibling

import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.ui.common.BaseUiState

data class ParkedCartUiState(
    val parkedSlots: List<ParkedCart?> = List(5) { null },
    val sheetOpen: Boolean = false,

    val overwriteSlot: Int? = null,
    val swapSlot: Int? = null,
    val activeCartIsEmpty: Boolean = true,

    override val error: String? = null,
) : BaseUiState {

    override val loading: Boolean get() = false
    val filledCount: Int get() = parkedSlots.count { it != null }
}
