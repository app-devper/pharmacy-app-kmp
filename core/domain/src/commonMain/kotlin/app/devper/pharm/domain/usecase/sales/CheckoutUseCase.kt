package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.repository.offlinesync.OfflineSaleQueue
import kotlinx.coroutines.CancellationException

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.domain.extension.looksLikeNetworkError
import app.devper.pharm.domain.extension.newClientRequestId
import app.devper.pharm.domain.param.offlinesync.EnqueueOfflineSaleParam
import app.devper.pharm.domain.validation.SaleValidationError

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.value.Money
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.ActiveCart
import app.devper.pharm.domain.model.CheckoutFailure
import app.devper.pharm.domain.model.CheckoutOutcome
import app.devper.pharm.domain.model.OversellShortfall
import app.devper.pharm.domain.param.sales.CheckoutLineParam
import app.devper.pharm.domain.param.sales.CheckoutParam
import app.devper.pharm.domain.param.sales.RunCheckoutParam
import app.devper.pharm.domain.repository.sales.CartRepository
import app.devper.pharm.domain.repository.sales.SaleRepository

class CheckoutUseCase(
    private val cart: CartRepository,
    private val sales: SaleRepository,
    private val offlineQueue: OfflineSaleQueue,
    dispatchers: AppDispatchers,
) : BaseUseCase<RunCheckoutParam, CheckoutOutcome>(dispatchers) {

    private data class Attempt(val request: CheckoutParam, val clientRequestId: String)
    private data class OversellConfirmation(
        val cart: ActiveCart,
        val received: Money,
        val kySkippedByCashier: Boolean,
    )

    private var pendingAttempt: Attempt? = null
    private var pendingOversell: OversellConfirmation? = null

    suspend operator fun invoke(
        received: Money,
        allowOversell: Boolean = false,
        kySkippedByCashier: Boolean = false,
    ): Result<CheckoutOutcome> = invoke(
        RunCheckoutParam(received, allowOversell, kySkippedByCashier),
    )

    override suspend fun execute(param: RunCheckoutParam): CheckoutOutcome {
        val snapshot = cart.state.value.active
        val lines = snapshot.items
        oversellOutcome(snapshot, param)?.let { return it }
        if (lines.isEmpty()) {
            throw CheckoutFailure(SaleValidationError.EmptyCart())
        }

        val checkoutParam = buildCheckoutParam(snapshot, param)

        val requestId = pendingAttempt
            ?.takeIf { it.request == checkoutParam }
            ?.clientRequestId
            ?: newClientRequestId()
        pendingAttempt = Attempt(checkoutParam, requestId)
        val request = checkoutParam.copy(clientRequestId = requestId)

        val serialized = serializeForOfflineQueue(request)
        val sale = try {
            sales.checkout(request)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (e.looksLikeNetworkError() && serialized != null) {
                offlineQueue.enqueue(EnqueueOfflineSaleParam(requestId, serialized))
                cart.clear()
                pendingAttempt = null
                return CheckoutOutcome.OfflineSaved
            }
            throw CheckoutFailure(e)
        }
        cart.commitReceipt(sale)
        pendingAttempt = null
        return CheckoutOutcome.Success(sale)
    }

    private fun oversellOutcome(snapshot: ActiveCart, param: RunCheckoutParam): CheckoutOutcome? {
        if (param.allowOversell) {
            val confirmation = pendingOversell
            pendingOversell = null
            return if (
                confirmation?.cart != snapshot ||
                confirmation.received != param.received ||
                confirmation.kySkippedByCashier != param.kySkippedByCashier
            ) CheckoutOutcome.CartChanged else null
        }

        val shortfalls = computeShortfalls(snapshot.items)
        pendingOversell = if (shortfalls.isNotEmpty()) {
            OversellConfirmation(snapshot, param.received, param.kySkippedByCashier)
        } else null
        return if (shortfalls.isNotEmpty()) CheckoutOutcome.NeedsOversellConfirm(shortfalls) else null
    }

    private fun buildCheckoutParam(snapshot: ActiveCart, param: RunCheckoutParam): CheckoutParam {
        val lines = snapshot.items
        val tier = snapshot.activeTier
        val subtotal = lines.fold(Money.Zero) { acc, line -> acc + line.lineTotal }
        val discountAmount = snapshot.cartDiscount.apply(subtotal)
        val oversoldDrugIds = if (param.allowOversell) {
            computeShortfalls(lines).map { it.drugId }.toSet()
        } else emptySet()

        return CheckoutParam(
            items = lines.map { line ->
                CheckoutLineParam(
                    drugId = line.drug.id,
                    qty = line.qty,
                    unitPrice = (line.basePrice - line.discount).coerceAtLeast(Money.Zero),
                    originalUnitPrice = line.basePrice,
                    itemDiscount = line.discount,
                    priceTier = tier.takeIf { it.isNotBlank() } ?: "",
                    allowOversell = line.drug.id in oversoldDrugIds,
                    unit = line.selectedUnit?.name.orEmpty(),
                    unitFactor = line.factor,
                )
            },
            received = param.received,
            customerId = snapshot.customer?.id,
            discount = discountAmount,
            priceTier = tier,
            kySkippedByCashier = param.kySkippedByCashier,
        )
    }

    private fun serializeForOfflineQueue(param: CheckoutParam): String? = try {
        sales.serializeCheckout(param)
    } catch (e: Exception) {
        null
    }

    private fun computeShortfalls(lines: List<CartLine>): List<OversellShortfall> =
        lines
            .groupBy { it.drug.id }
            .mapNotNull { (_, group) ->
                val drug = group.first().drug
                val totalQty = group.sumOf { it.qty }
                val available = drug.stock.value.coerceAtLeast(0)
                if (totalQty > available) {
                    OversellShortfall(drug.id, drug.name, totalQty, available)
                } else null
            }
}
