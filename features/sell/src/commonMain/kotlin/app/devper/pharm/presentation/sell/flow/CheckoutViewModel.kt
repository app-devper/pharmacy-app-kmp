package app.devper.pharm.presentation.sell.flow

import androidx.lifecycle.viewModelScope
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
import app.devper.pharm.domain.usecase.CheckoutUseCase
import app.devper.pharm.domain.usecase.ClearCartUseCase
import app.devper.pharm.domain.usecase.DismissReceiptUseCase
import app.devper.pharm.domain.usecase.EnqueueOfflineSaleUseCase
import app.devper.pharm.domain.usecase.SubmitKyFormsUseCase
import app.devper.pharm.domain.util.KyRequiredCalculator
import app.devper.pharm.domain.util.looksLikeNetworkError
import app.devper.pharm.domain.util.newClientRequestId
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.ui.common.BaseViewModel
import app.devper.pharm.ui.print.buildReceiptTemplate
import app.devper.pharm.presentation.sell.internal.todayYmd
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CheckoutViewModel(
    cartState: CartStateProvider,
    settings: SettingsProvider,
    private val checkout: CheckoutUseCase,
    private val clearCart: ClearCartUseCase,
    private val dismissReceiptUseCase: DismissReceiptUseCase,
    private val submitKyForms: SubmitKyFormsUseCase,
    private val enqueueOfflineSale: EnqueueOfflineSaleUseCase,
    private val receiptPrinter: ReceiptPrinter,
) : BaseViewModel<CheckoutUiState>(CheckoutUiState()) {

    private data class ReceiptSnapshot(
        val cart: List<CartLine>,
        val customer: Customer?,
        val received: Double,
    )

    private var pendingClientRequestId: String? = null
    private var pendingKyFields: KyCaptureFields? = null
    private var pendingKyRequired: KyRequired? = null
    private var pendingKySkippedByCashier: Boolean = false
    private var receiptSnapshot: ReceiptSnapshot? = null

    private var lastCart: List<CartLine> = emptyList()
    private var lastCustomer: Customer? = null
    private var lastReceivedNum: Double = 0.0
    private var lastSettings: Settings = Settings()

    init {

        cartState.state
            .onEach { snap ->
                lastCart = snap.items
                lastCustomer = snap.selectedCustomer
                val received = snap.cashReceived.toDoubleOrNull() ?: 0.0
                lastReceivedNum = received
                setState {
                    copy(
                        cartIsEmpty = snap.isEmpty,
                        tenderOk = received >= snap.total,
                    )
                }
            }
            .launchIn(viewModelScope)
        settings.state
            .onEach { lastSettings = it }
            .launchIn(viewModelScope)
    }

    fun submit() {
        if (!current.canCheckout) return

        val required = KyRequiredCalculator.calculate(lastCart)
        if (!required.isEmpty) {
            if (lastSettings.ky.skipAuto) {
                pendingKyRequired = null
                pendingKyFields = null
                pendingKySkippedByCashier = true
            } else {
                pendingKyRequired = required
                setState { copy(kyCapturePending = required) }
                return
            }
        }
        startNewCheckout(allowOversell = false)
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
        setState { copy(kyCapturePending = null, showSkipKyConfirm = false) }
        startNewCheckout(allowOversell = false)
    }

    fun dismissKyCapture() {
        pendingKyFields = null
        pendingKyRequired = null
        pendingKySkippedByCashier = false
        setState { copy(kyCapturePending = null, showSkipKyConfirm = false) }
    }

    fun confirmOversell() {
        setState { copy(oversellPending = null) }

        runCheckout(allowOversell = true)
    }

    fun dismissOversell() {
        setState { copy(oversellPending = null) }
    }

    fun dismissError() = setState { copy(error = null) }

    fun dismissReceipt() {
        dismissReceiptUseCase()
        receiptSnapshot = null
        setState { copy(lastReceiptTemplate = null) }
    }

    fun printLastReceipt(sale: Sale) {
        val template = current.lastReceiptTemplate ?: return
        if (!receiptPrinter.print(template)) {
            setState { copy(error = "พิมพ์ใบเสร็จไม่สำเร็จ — แพลตฟอร์มนี้ยังไม่รองรับ") }
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
        val tzAtSubmit = lastSettings.timezone
        val cartSnapshot = lastCart
        val customerSnapshot = lastCustomer
        val receivedSnapshot = lastReceivedNum

        setState { copy(checkingOut = true, error = null) }
        launchResult(
            block = { checkout(receivedSnapshot, allowOversell, requestId, kySkippedAtSubmit) },
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
        tz: String,
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
            soldAtFormatted = todayYmd(tz),
        )
        setState { copy(checkingOut = false, lastReceiptTemplate = template) }

        if (kyRequired != null && kyFields != null) {
            submitKyForms(
                sale = sale,
                required = kyRequired,
                captured = kyFields,
                dateYmd = todayYmd(tz),
            ).fold(
                onSuccess = { result ->
                    if (result.anyFailed) {
                        setState {
                            copy(
                                error = "บิล ${sale.billNo} บันทึกแล้ว แต่บันทึก ขย. ไม่ครบ:\n" +
                                    result.failed.joinToString("\n"),
                            )
                        }
                    }
                },
                onFailure = { e ->
                    setState {
                        copy(
                            error = "บิล ${sale.billNo} บันทึกแล้ว แต่บันทึก ขย. ผิดพลาด: ${e.message ?: "ไม่ทราบสาเหตุ"}",
                        )
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
                copy(
                    checkingOut = false,
                    error = "เครือข่ายไม่ได้เชื่อมต่อ — บิลถูกเก็บไว้เพื่อซิงค์ภายหลัง",
                )
            }
        } else {
            clearPendingTokens()
            setState {
                copy(
                    checkingOut = false,
                    error = cause.message ?: "ออกใบเสร็จไม่สำเร็จ",
                )
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
