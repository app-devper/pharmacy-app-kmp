package app.devper.pharm.data.repository

import app.devper.pharm.data.storage.ParkedCartStorage
import app.devper.pharm.domain.model.ActiveCart
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartLineKey
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

    private val _active = MutableStateFlow(ActiveCart.Empty)
    override val active: StateFlow<ActiveCart> = _active.asStateFlow()

    private val _lastReceipt = MutableStateFlow<Sale?>(null)
    override val lastReceipt: StateFlow<Sale?> = _lastReceipt.asStateFlow()

    private val _parkedSlots = MutableStateFlow<List<ParkedCart?>>(
        parkedCartStorage.loadAll(),
    )
    override val parkedSlots: StateFlow<List<ParkedCart?>> = _parkedSlots.asStateFlow()

    override fun add(param: AddCartItemParam) {
        val factor = param.altUnit?.factor?.coerceAtLeast(1) ?: 1
        val key = CartLineKey(param.drug.id, param.altUnit?.name)
        _active.update { current ->
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
        _active.update { current ->
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
        _active.update { current ->
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
        _active.update { current ->
            current.copy(items = current.items.filterNot { it.key == key })
        }
    }

    override fun selectCustomer(customer: Customer) {
        val tier = customer.priceTier.takeIf { it.isNotBlank() } ?: Tier.Retail
        _active.update { current ->
            current.copy(
                customer = customer,
                activeTier = tier,
                items = current.items.map { it.copy(tier = tier) },
            )
        }
    }

    override fun clearCustomer() {
        _active.update { current ->
            current.copy(
                customer = null,
                activeTier = Tier.Retail,
                items = current.items.map { it.copy(tier = Tier.Retail) },
            )
        }
    }

    override fun setCartDiscount(discount: CartDiscount) {
        _active.update { it.copy(cartDiscount = discount) }
    }

    override fun setCashReceived(value: String) {
        _active.update { it.copy(cashReceived = value) }
    }

    override fun commitReceipt(sale: Sale) {
        _lastReceipt.value = sale
        _active.value = ActiveCart.Empty
    }

    override fun dismissReceipt() {
        _lastReceipt.value = null
    }

    override fun clear() {
        _lastReceipt.value = null
        _active.value = ActiveCart.Empty
    }

    override fun parkCart(slot: Int) {
        if (!isValidSlot(slot)) return
        val snapshot = _active.value
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
        _active.value = ActiveCart.Empty
    }

    override fun restoreCart(slot: Int) {
        if (!isValidSlot(slot)) return
        val parked = _parkedSlots.value.getOrNull(slot) ?: return

        _lastReceipt.value = null
        _active.value = ActiveCart(
            items = parked.items,
            customer = parked.customer,
            cartDiscount = parked.cartDiscount,
            activeTier = parked.activeTier,
            cashReceived = parked.cashReceived,
        )

        parkedCartStorage.clear(slot)
        _parkedSlots.update { current -> current.replaceAt(slot, null) }
    }

    override fun discardSlot(slot: Int) {
        if (!isValidSlot(slot)) return
        parkedCartStorage.clear(slot)
        _parkedSlots.update { current -> current.replaceAt(slot, null) }
    }

    private fun isValidSlot(slot: Int) = slot in 0 until PARK_SLOT_COUNT

    private fun <T> List<T>.replaceAt(index: Int, value: T): List<T> =
        toMutableList().apply { this[index] = value }
}
