package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.domain.model.OversellShortfall
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.LoadableUiState

data class CheckoutUiState(
    val checkingOut: Boolean = false,
    val kyCapturePending: KyRequired? = null,
    val showSkipKyConfirm: Boolean = false,
    val oversellPending: List<OversellShortfall>? = null,
    val lastReceiptTemplate: ReceiptTemplate? = null,
    val errorState: AppException? = null,

    val cartIsEmpty: Boolean = true,

    val tenderOk: Boolean = false,
) : LoadableUiState<CheckoutUiState> {

    override val loading: Boolean get() = checkingOut

    override fun withLoading(value: Boolean) = copy(checkingOut = value)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val canCheckout: Boolean get() =
        !cartIsEmpty &&
            tenderOk &&
            !checkingOut &&
            kyCapturePending == null &&
            !showSkipKyConfirm &&
            oversellPending == null
}
