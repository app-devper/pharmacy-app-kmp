package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.sell.exception.CheckoutUiStateError

import androidx.lifecycle.viewModelScope
import app.devper.pharm.common.value.Money
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CheckoutFailure
import app.devper.pharm.domain.model.CheckoutOutcome
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.observer.CartStateProvider
import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.usecase.sales.CheckoutUseCase
import app.devper.pharm.domain.usecase.sales.ClearCartUseCase
import app.devper.pharm.domain.usecase.sales.DismissReceiptUseCase
import app.devper.pharm.domain.usecase.sales.SetCashReceivedUseCase
import app.devper.pharm.domain.usecase.offlinesync.EnqueueOfflineSaleUseCase
import app.devper.pharm.domain.usecase.ky.SubmitKyFormsUseCase
import app.devper.pharm.domain.extension.calculateKyRequired
import app.devper.pharm.domain.extension.looksLikeNetworkError
import app.devper.pharm.domain.extension.newClientRequestId
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.ui.common.BaseLoadableViewModel
import app.devper.pharm.ui.format.todayBuddhistDisplay
import app.devper.pharm.ui.print.buildReceiptTemplate
import app.devper.pharm.ui.format.todayLocalDate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class CheckoutViewModel(
    private val cartState: CartStateProvider,
    settings: SettingsProvider,
    private val timeZoneProvider: TimeZoneProvider,
    private val checkout: CheckoutUseCase,
    private val clearCart: ClearCartUseCase,
    private val dismissReceiptUseCase: DismissReceiptUseCase,
    private val submitKyForms: SubmitKyFormsUseCase,
    private val enqueueOfflineSale: EnqueueOfflineSaleUseCase,
    private val setCashReceived: SetCashReceivedUseCase,
    private val receiptPrinter: ReceiptPrinter,
) : BaseLoadableViewModel<CheckoutUiState>(CheckoutUiState()) {

    private data class ReceiptSnapshot(
        val cart: List<CartLine>,
        val customer: Customer?,
        val received: Double,
    )

    private var pendingClientRequestId: String? = null
    private var pendingKyFields: KyCaptureFields? = null
    private var pendingKyRequired: KyRequired? = null
    private var pendingKySkippedByCashier: Boolean = false
    private var precaptureItems: List<CartLine>? = null
    private var receiptSnapshot: ReceiptSnapshot? = null

    private var lastSettings: Settings = Settings()

    init {

        cartState.state
            .onEach { snap ->
                val invalidatePrecapture = current.kyCaptured && snap.items != precaptureItems
                if (invalidatePrecapture) {
                    pendingKyFields = null
                    precaptureItems = null
                }
                setState {
                    if (invalidatePrecapture) copy(cartIsEmpty = snap.isEmpty, kyCaptured = false, kyPrecaptureInvalidated = true)
                    else copy(cartIsEmpty = snap.isEmpty)
                }
            }
            .launchIn(viewModelScope)
        settings.state
            .onEach { lastSettings = it }
            .launchIn(viewModelScope)
    }

    fun openPayment() {
        if (!current.canCheckout) return
        setState { copy(paymentOpen = true) }
    }

    fun closePayment() {
        if (current.checkingOut) return
        setState { copy(paymentOpen = false) }
    }

    fun submitExact() {
        if (!current.canCheckout) return
        setCashReceived(plainAmount(cartState.current.total.amount))
        submit()
    }

    fun submit() {
        if (!current.canCheckout) return

        val snap = cartState.current
        val received = snap.cashReceived.toDoubleOrNull() ?: 0.0
        if (received < snap.total.amount) return

        val required = snap.items.calculateKyRequired()
        if (!required.isEmpty) {
            if (lastSettings.ky.skipAuto) {
                pendingKyRequired = null
                pendingKyFields = null
                pendingKySkippedByCashier = true
            } else if (pendingKyFields != null) {
                pendingKyRequired = required
            } else {
                pendingKyRequired = required
                setState { copy(kyCapturePending = required) }
                return
            }
        }
        startNewCheckout(allowOversell = false)
    }

    fun openKyPrecapture() {
        val required = cartState.current.items.calculateKyRequired()
        if (required.isEmpty) return
        setState { copy(kyPrecapture = required, kyPrecaptureInvalidated = false) }
    }

    fun confirmKyPrecapture(fields: KyCaptureFields) {
        pendingKyFields = fields
        precaptureItems = cartState.current.items
        setState { copy(kyPrecapture = null, kyCaptured = true, capturedKyFields = fields) }
    }

    fun dismissKyPrecapture() {
        setState { copy(kyPrecapture = null) }
    }

    fun confirmKyCapture(fields: KyCaptureFields) {
        pendingKyFields = fields
        setState { copy(kyCapturePending = null) }
        startNewCheckout(allowOversell = false)
    }

    fun requestSkipKy() {
        if (current.kyCapturePending == null) return
        setState { copy(showSkipKyConfirm = true) }
    }

    fun cancelSkipKy() {
        setState { copy(showSkipKyConfirm = false) }
    }

    fun confirmSkipKy() {
        pendingKyFields = null
        pendingKyRequired = null
        pendingKySkippedByCashier = true
        precaptureItems = null
        setState { copy(kyCapturePending = null, showSkipKyConfirm = false, kyCaptured = false, capturedKyFields = null) }
        startNewCheckout(allowOversell = false)
    }

    fun dismissKyCapture() {
        pendingKyFields = null
        pendingKyRequired = null
        pendingKySkippedByCashier = false
        precaptureItems = null
        setState { copy(kyCapturePending = null, showSkipKyConfirm = false, kyCaptured = false, capturedKyFields = null) }
    }

    fun confirmOversell() {
        setState { copy(oversellPending = null) }

        runCheckout(allowOversell = true)
    }

    fun dismissOversell() {
        setState { copy(oversellPending = null) }
    }

    fun dismissReceipt() {
        dismissReceiptUseCase()
        receiptSnapshot = null
        setState { copy(lastReceiptTemplate = null) }
    }

    fun printLastReceipt(sale: Sale) {
        val template = current.lastReceiptTemplate ?: return
        if (!receiptPrinter.print(template)) {
            setState { copy(errorState = CheckoutUiStateError.PrintReceiptUnsupported()) }
        }
    }

    private fun startNewCheckout(allowOversell: Boolean) {

        pendingClientRequestId = newClientRequestId()
        runCheckout(allowOversell = allowOversell)
    }

    private fun runCheckout(allowOversell: Boolean) {
        val requestId = pendingClientRequestId

        val kyRequiredAtSubmit = pendingKyRequired
        val kyFieldsAtSubmit = pendingKyFields
        val kySkippedAtSubmit = pendingKySkippedByCashier
        val tzAtSubmit = timeZoneProvider.current
        val snap = cartState.current
        val cartSnapshot = snap.items
        val customerSnapshot = snap.selectedCustomer
        val receivedSnapshot = snap.cashReceived.toDoubleOrNull() ?: 0.0

        setState { copy(checkingOut = true, errorState = null) }
        launchResult(
            block = { checkout(Money(receivedSnapshot), allowOversell, requestId, kySkippedAtSubmit) },
            onSuccess = { outcome ->
                when (outcome) {
                    is CheckoutOutcome.Success -> handleSuccess(
                        sale = outcome.sale,
                        kyRequired = kyRequiredAtSubmit,
                        kyFields = kyFieldsAtSubmit,
                        tz = tzAtSubmit,
                        cart = cartSnapshot,
                        customer = customerSnapshot,
                        received = receivedSnapshot,
                    )
                    is CheckoutOutcome.NeedsOversellConfirm ->
                        setState { copy(checkingOut = false, oversellPending = outcome.shortfalls) }
                }
            },
            onFailure = ::handleFailure,
        )
    }

    private suspend fun handleSuccess(
        sale: Sale,
        kyRequired: KyRequired?,
        kyFields: KyCaptureFields?,
        tz: kotlinx.datetime.TimeZone,
        cart: List<CartLine>,
        customer: Customer?,
        received: Double,
    ) {
        clearPendingTokens()
        receiptSnapshot = ReceiptSnapshot(cart = cart, customer = customer, received = received)
        val template = buildReceiptTemplate(
            sale = sale,
            cartSnapshot = cart,
            customer = customer,
            settings = lastSettings,
            received = received,
            soldAtFormatted = todayBuddhistDisplay(tz),
        )
        setState { copy(checkingOut = false, paymentOpen = false, lastReceiptTemplate = template) }

        if (kyRequired != null && kyFields != null) {
            submitKyForms(
                sale = sale,
                required = kyRequired,
                captured = kyFields,
                date = todayLocalDate(tz),
            ).fold(
                onSuccess = { result ->
                    if (result.anyFailed) {
                        setState {
                            copy(errorState = CheckoutUiStateError.KyIncomplete(sale.billNo, result.failed))
                        }
                    }
                },
                onFailure = { e ->
                    setState {
                        copy(errorState = CheckoutUiStateError.KyError(sale.billNo, e))
                    }
                },
            )
        }
    }

    private fun handleFailure(error: Throwable) {

        val cf = error as? CheckoutFailure
        val cause = cf?.cause ?: error
        val payload = cf?.serializedRequest
        val crid = cf?.clientRequestId
        if (cause.looksLikeNetworkError() && payload != null && crid != null) {

            enqueueOfflineSale(crid, payload)
            clearCart()
            clearPendingTokens()
            setState {
                copy(checkingOut = false, paymentOpen = false, errorState = CheckoutUiStateError.OfflineSaved())
            }
        } else {
            clearPendingTokens()
            setState {
                copy(checkingOut = false, errorState = (cause as? AppException) ?: CheckoutUiStateError.CheckoutFailed(cause))
            }
        }
    }

    private fun clearPendingTokens() {
        pendingClientRequestId = null
        pendingKyRequired = null
        pendingKyFields = null
        pendingKySkippedByCashier = false
    }
}

internal fun plainAmount(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
