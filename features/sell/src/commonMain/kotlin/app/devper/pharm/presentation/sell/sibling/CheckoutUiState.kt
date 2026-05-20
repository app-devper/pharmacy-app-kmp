package app.devper.pharm.presentation.sell.sibling

import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.domain.model.OversellShortfall
import app.devper.pharm.ui.common.BaseUiState

data class CheckoutUiState(
    val checkingOut: Boolean = false,
    val kyCapturePending: KyRequired? = null,
    val showSkipKyConfirm: Boolean = false,
    val oversellPending: List<OversellShortfall>? = null,
    override val error: String? = null,

    val cartIsEmpty: Boolean = true,

    val tenderOk: Boolean = false,
) : BaseUiState {

    override val loading: Boolean get() = checkingOut

    val canCheckout: Boolean get() =
        !cartIsEmpty &&
            tenderOk &&
            !checkingOut &&
            kyCapturePending == null &&
            !showSkipKyConfirm &&
            oversellPending == null
}
