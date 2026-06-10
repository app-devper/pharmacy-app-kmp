package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.ui.common.BaseUiState

data class ParkedCartUiState(
    val parkedSlots: List<ParkedCart?> = List(5) { null },
    val selectedSlot: Int = 0,
    val sheetOpen: Boolean = false,

    val overwriteSlot: Int? = null,
    val swapSlot: Int? = null,
    val activeCartIsEmpty: Boolean = true,
) : BaseUiState {

    override val loading: Boolean get() = false
    val filledCount: Int get() = parkedSlots.count { it != null }
}
