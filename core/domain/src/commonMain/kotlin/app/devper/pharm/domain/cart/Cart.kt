package app.devper.pharm.domain.cart

import app.devper.pharm.common.value.Money
import app.devper.pharm.domain.extension.Tier
import app.devper.pharm.domain.model.ActiveCart
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.model.CartSnapshot
import app.devper.pharm.domain.model.CartState
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.domain.model.Sale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

const val PARK_SLOT_COUNT = 5

@OptIn(ExperimentalTime::class)
class Cart(
    private val store: CartStore,
    private val now: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) {
    private val _state = MutableStateFlow(store.loadActive()?.let { CartState(active = it) } ?: CartState.Empty)
    val state: StateFlow<CartState> = _state.asStateFlow()

    val snapshots: Flow<CartSnapshot> = state.map { it.snapshot() }
    val current: CartSnapshot get() = _state.value.snapshot()

    private val _parkedSlots = MutableStateFlow(store.loadParked())
    val parkedSlots: StateFlow<List<ParkedCart?>> = _parkedSlots.asStateFlow()

    fun add(drug: Drug, altUnit: AltUnit? = null) {
        val factor = altUnit?.factor?.coerceAtLeast(1) ?: 1
        val key = CartLineKey(drug.id, altUnit?.name)
        mutateActive { current ->
            val existing = current.items.firstOrNull { it.key == key }
            val items = if (existing != null) {
                current.items.map { if (it.key == key) it.copy(qty = it.qty + factor) else it }
            } else {
                current.items + CartLine(drug = drug, qty = factor, tier = current.activeTier, selectedUnit = altUnit)
            }
            current.copy(items = items)
        }
    }

    fun setQty(key: CartLineKey, displayQty: Int) {
        mutateActive { current ->
            val items = if (displayQty <= 0) {
                current.items.filterNot { it.key == key }
            } else {
                current.items.map { if (it.key == key) it.copy(qty = displayQty * it.factor) else it }
            }
            current.copy(items = items)
        }
    }

    fun setLineDiscount(key: CartLineKey, discount: Double) {
        mutateActive { current ->
            current.copy(
                items = current.items.map {
                    if (it.key == key) it.copy(discount = Money(discount.coerceIn(0.0, it.basePrice.amount))) else it
                },
            )
        }
    }

    fun remove(key: CartLineKey) {
        mutateActive { current -> current.copy(items = current.items.filterNot { it.key == key }) }
    }

    fun selectCustomer(customer: Customer) {
        val tier = customer.priceTier.takeIf { it.isNotBlank() } ?: Tier.Retail
        mutateActive { current -> current.repriced(tier).copy(customer = customer) }
    }

    fun clearCustomer() {
        mutateActive { current -> current.repriced(Tier.Retail).copy(customer = null) }
    }

    fun setCartDiscount(discount: CartDiscount) {
        mutateActive { it.copy(cartDiscount = discount) }
    }

    fun setCashReceived(value: String) {
        mutateActive { it.copy(cashReceived = value) }
    }

    fun commitReceipt(sale: Sale) {
        _state.value = CartState(active = ActiveCart.Empty, lastReceipt = sale)
        persistActive(ActiveCart.Empty)
    }

    fun dismissReceipt() {
        _state.update { it.copy(lastReceipt = null) }
    }

    fun clear() {
        _state.value = CartState.Empty
        persistActive(ActiveCart.Empty)
    }

    fun park(slot: Int) {
        if (!isValidSlot(slot)) return
        val active = _state.value.active
        if (active.items.isEmpty()) return
        val parked = ParkedCart(
            items = active.items,
            customer = active.customer,
            cartDiscount = active.cartDiscount,
            activeTier = active.activeTier,
            cashReceived = active.cashReceived,
            parkedAt = now(),
        )
        store.saveParked(slot, parked)
        _parkedSlots.update { it.replaceAt(slot, parked) }
        mutateActive { ActiveCart.Empty }
    }

    fun restore(slot: Int) {
        if (!isValidSlot(slot)) return
        val parked = _parkedSlots.value.getOrNull(slot) ?: return
        val active = ActiveCart(
            items = parked.items,
            customer = parked.customer,
            cartDiscount = parked.cartDiscount,
            activeTier = parked.activeTier,
            cashReceived = parked.cashReceived,
        )
        _state.value = CartState(active = active, lastReceipt = null)
        persistActive(active)
        store.clearParked(slot)
        _parkedSlots.update { it.replaceAt(slot, null) }
    }

    fun discard(slot: Int) {
        if (!isValidSlot(slot)) return
        store.clearParked(slot)
        _parkedSlots.update { it.replaceAt(slot, null) }
    }

    private fun ActiveCart.repriced(tier: String) =
        copy(activeTier = tier, items = items.map { it.copy(tier = tier) })

    private inline fun mutateActive(crossinline transform: (ActiveCart) -> ActiveCart) {
        _state.update { it.copy(active = transform(it.active)) }
        persistActive(_state.value.active)
    }

    private fun persistActive(active: ActiveCart) {
        if (active.items.isEmpty()) store.clearActive() else store.saveActive(active)
    }

    private fun isValidSlot(slot: Int) = slot in 0 until PARK_SLOT_COUNT

    private fun <T> List<T>.replaceAt(index: Int, value: T): List<T> =
        mapIndexed { i, old -> if (i == index) value else old }
}

private fun CartState.snapshot() = CartSnapshot(
    items = active.items,
    selectedCustomer = active.customer,
    cartDiscount = active.cartDiscount,
    activeTier = active.activeTier,
    cashReceived = active.cashReceived,
    lastReceipt = lastReceipt,
)
