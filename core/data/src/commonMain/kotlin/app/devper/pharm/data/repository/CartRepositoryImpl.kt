package app.devper.pharm.data.repository

import app.devper.pharm.data.storage.ParkedCartStorage
import app.devper.pharm.domain.model.ActiveCart
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.model.CartState
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.AddCartItemParam
import app.devper.pharm.domain.param.SetCartQtyParam
import app.devper.pharm.domain.param.SetLineDiscountParam
import app.devper.pharm.domain.pricing.Tier
import app.devper.pharm.domain.repository.CartRepository
import app.devper.pharm.domain.repository.PARK_SLOT_COUNT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class CartRepositoryImpl(
    private val parkedCartStorage: ParkedCartStorage,
) : CartRepository {

    private val _state = MutableStateFlow(
        parkedCartStorage.loadActive()?.let { CartState(active = it) } ?: CartState.Empty,
    )
    override val state: StateFlow<CartState> = _state.asStateFlow()

    private val _parkedSlots = MutableStateFlow<List<ParkedCart?>>(
        parkedCartStorage.loadAll(),
    )
    override val parkedSlots: StateFlow<List<ParkedCart?>> = _parkedSlots.asStateFlow()

    override fun add(param: AddCartItemParam) {
        val factor = param.altUnit?.factor?.coerceAtLeast(1) ?: 1
        val key = CartLineKey(param.drug.id, param.altUnit?.name)
        mutateActive { current ->
            val tier = current.activeTier
            val idx = current.items.indexOfFirst { it.key == key }
            val newItems = if (idx >= 0) {
                val existing = current.items[idx]
                current.items.toMutableList().apply {
                    this[idx] = existing.copy(qty = existing.qty + factor)
                }
            } else {
                current.items + CartLine(
                    drug = param.drug,
                    qty = factor,
                    tier = tier,
                    selectedUnit = param.altUnit,
                )
            }
            current.copy(items = newItems)
        }
    }

    override fun setQty(param: SetCartQtyParam) {
        mutateActive { current ->
            val idx = current.items.indexOfFirst { it.key == param.key }
            val newItems = when {
                idx < 0 -> current.items
                param.displayQty <= 0 -> current.items.toMutableList().apply { removeAt(idx) }
                else -> {
                    val existing = current.items[idx]
                    val baseQty = param.displayQty * existing.factor
                    current.items.toMutableList().apply {
                        this[idx] = existing.copy(qty = baseQty)
                    }
                }
            }
            current.copy(items = newItems)
        }
    }

    override fun setLineDiscount(param: SetLineDiscountParam) {
        mutateActive { current ->
            val idx = current.items.indexOfFirst { it.key == param.key }
            val newItems = if (idx < 0) current.items
            else {
                val existing = current.items[idx]
                val capped = param.discount.coerceIn(0.0, existing.basePrice)
                current.items.toMutableList().apply {
                    this[idx] = existing.copy(discount = capped)
                }
            }
            current.copy(items = newItems)
        }
    }

    override fun remove(key: CartLineKey) {
        mutateActive { current ->
            current.copy(items = current.items.filterNot { it.key == key })
        }
    }

    override fun selectCustomer(customer: Customer) {
        val tier = customer.priceTier.takeIf { it.isNotBlank() } ?: Tier.Retail
        mutateActive { current ->
            current.copy(
                customer = customer,
                activeTier = tier,
                items = current.items.map { it.copy(tier = tier) },
            )
        }
    }

    override fun clearCustomer() {
        mutateActive { current ->
            current.copy(
                customer = null,
                activeTier = Tier.Retail,
                items = current.items.map { it.copy(tier = Tier.Retail) },
            )
        }
    }

    override fun setCartDiscount(discount: CartDiscount) {
        mutateActive { it.copy(cartDiscount = discount) }
    }

    override fun setCashReceived(value: String) {
        mutateActive { it.copy(cashReceived = value) }
    }

    override fun commitReceipt(sale: Sale) {
        _state.value = CartState(active = ActiveCart.Empty, lastReceipt = sale)
        persistActive(ActiveCart.Empty)
    }

    override fun dismissReceipt() {
        _state.update { it.copy(lastReceipt = null) }
    }

    override fun clear() {
        _state.value = CartState.Empty
        persistActive(ActiveCart.Empty)
    }

    override fun parkCart(slot: Int) {
        if (!isValidSlot(slot)) return
        val snapshot = _state.value.active
        if (snapshot.items.isEmpty()) return

        val parked = ParkedCart(
            items = snapshot.items,
            customer = snapshot.customer,
            cartDiscount = snapshot.cartDiscount,
            activeTier = snapshot.activeTier,
            cashReceived = snapshot.cashReceived,
            parkedAt = Clock.System.now().toEpochMilliseconds(),
        )
        parkedCartStorage.save(slot, parked)
        _parkedSlots.update { current -> current.replaceAt(slot, parked) }
        mutateActive { ActiveCart.Empty }
    }

    override fun restoreCart(slot: Int) {
        if (!isValidSlot(slot)) return
        val parked = _parkedSlots.value.getOrNull(slot) ?: return

        _state.value = CartState(
            active = ActiveCart(
                items = parked.items,
                customer = parked.customer,
                cartDiscount = parked.cartDiscount,
                activeTier = parked.activeTier,
                cashReceived = parked.cashReceived,
            ),
            lastReceipt = null,
        )
        persistActive(_state.value.active)

        parkedCartStorage.clear(slot)
        _parkedSlots.update { current -> current.replaceAt(slot, null) }
    }

    override fun discardSlot(slot: Int) {
        if (!isValidSlot(slot)) return
        parkedCartStorage.clear(slot)
        _parkedSlots.update { current -> current.replaceAt(slot, null) }
    }

    private inline fun mutateActive(crossinline transform: (ActiveCart) -> ActiveCart) {
        _state.update { it.copy(active = transform(it.active)) }
        persistActive(_state.value.active)
    }

    private fun persistActive(active: ActiveCart) {
        if (active.items.isEmpty()) parkedCartStorage.clearActive()
        else parkedCartStorage.saveActive(active)
    }

    private fun isValidSlot(slot: Int) = slot in 0 until PARK_SLOT_COUNT

    private fun <T> List<T>.replaceAt(index: Int, value: T): List<T> =
        toMutableList().apply { this[index] = value }
}
