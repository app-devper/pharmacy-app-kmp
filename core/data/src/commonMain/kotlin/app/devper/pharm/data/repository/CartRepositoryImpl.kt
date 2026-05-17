package app.devper.pharm.data.repository

import app.devper.pharm.data.storage.ParkedCartStorage
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

    private val _items = MutableStateFlow<List<CartLine>>(emptyList())
    override val items: StateFlow<List<CartLine>> = _items.asStateFlow()

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    override val selectedCustomer: StateFlow<Customer?> = _selectedCustomer.asStateFlow()

    private val _cartDiscount = MutableStateFlow<CartDiscount>(CartDiscount.None)
    override val cartDiscount: StateFlow<CartDiscount> = _cartDiscount.asStateFlow()

    private val _activeTier = MutableStateFlow(Tier.Retail)
    override val activeTier: StateFlow<String> = _activeTier.asStateFlow()

    private val _cashReceived = MutableStateFlow("")
    override val cashReceived: StateFlow<String> = _cashReceived.asStateFlow()

    private val _lastReceipt = MutableStateFlow<Sale?>(null)
    override val lastReceipt: StateFlow<Sale?> = _lastReceipt.asStateFlow()

    private val _parkedSlots = MutableStateFlow<List<ParkedCart?>>(

        parkedCartStorage.loadAll(),
    )
    override val parkedSlots: StateFlow<List<ParkedCart?>> = _parkedSlots.asStateFlow()

    override fun add(param: AddCartItemParam) {
        val factor = param.altUnit?.factor?.coerceAtLeast(1) ?: 1
        val key = CartLineKey(param.drug.id, param.altUnit?.name)
        _items.update { current ->
            val tier = _activeTier.value
            val idx = current.indexOfFirst { it.key == key }
            if (idx >= 0) {
                val existing = current[idx]
                current.toMutableList().apply { this[idx] = existing.copy(qty = existing.qty + factor) }
            } else {
                current + CartLine(
                    drug = param.drug,
                    qty = factor,
                    tier = tier,
                    selectedUnit = param.altUnit,
                )
            }
        }
    }

    override fun setQty(param: SetCartQtyParam) {
        _items.update { current ->
            val idx = current.indexOfFirst { it.key == param.key }
            if (idx < 0) current
            else if (param.displayQty <= 0) current.toMutableList().apply { removeAt(idx) }
            else {
                val existing = current[idx]
                val baseQty = param.displayQty * existing.factor
                current.toMutableList().apply { this[idx] = existing.copy(qty = baseQty) }
            }
        }
    }

    override fun setLineDiscount(param: SetLineDiscountParam) {
        _items.update { current ->
            val idx = current.indexOfFirst { it.key == param.key }
            if (idx < 0) current
            else {
                val existing = current[idx]

                val capped = param.discount.coerceIn(0.0, existing.basePrice)
                current.toMutableList().apply { this[idx] = existing.copy(discount = capped) }
            }
        }
    }

    override fun remove(key: CartLineKey) {
        _items.update { it.filterNot { line -> line.key == key } }
    }

    override fun selectCustomer(customer: Customer) {
        _selectedCustomer.value = customer
        applyTier(customer.priceTier.takeIf { it.isNotBlank() } ?: Tier.Retail)
    }

    override fun clearCustomer() {
        _selectedCustomer.value = null
        applyTier(Tier.Retail)
    }

    override fun setCartDiscount(discount: CartDiscount) {
        _cartDiscount.value = discount
    }

    override fun setCashReceived(value: String) {
        _cashReceived.value = value
    }

    override fun commitReceipt(sale: Sale) {
        _lastReceipt.value = sale
        clearActive()
    }

    override fun dismissReceipt() {
        _lastReceipt.value = null
    }

    override fun clear() {
        clearActive()
        _lastReceipt.value = null
    }

    override fun parkCart(slot: Int) {
        if (!isValidSlot(slot)) return
        val items = _items.value
        if (items.isEmpty()) return

        val parked = ParkedCart(
            items = items,
            customer = _selectedCustomer.value,
            cartDiscount = _cartDiscount.value,
            activeTier = _activeTier.value,
            cashReceived = _cashReceived.value,
            parkedAt = Clock.System.now().toEpochMilliseconds(),
        )
        parkedCartStorage.save(slot, parked)
        _parkedSlots.update { current -> current.replaceAt(slot, parked) }
        clearActive()
    }

    override fun restoreCart(slot: Int) {
        if (!isValidSlot(slot)) return
        val parked = _parkedSlots.value.getOrNull(slot) ?: return

        _lastReceipt.value = null
        replaceActive(
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

    private fun clearActive() {
        replaceActive(
            items = emptyList(),
            customer = null,
            cartDiscount = CartDiscount.None,
            activeTier = Tier.Retail,
            cashReceived = "",
        )
    }

    private fun replaceActive(
        items: List<CartLine>,
        customer: Customer?,
        cartDiscount: CartDiscount,
        activeTier: String,
        cashReceived: String,
    ) {
        _items.value = items
        _selectedCustomer.value = customer
        _cartDiscount.value = cartDiscount
        _activeTier.value = activeTier
        _cashReceived.value = cashReceived
    }

    private fun applyTier(tier: String) {
        _activeTier.value = tier
        _items.update { current -> current.map { it.copy(tier = tier) } }
    }

    private fun <T> List<T>.replaceAt(index: Int, value: T): List<T> =
        toMutableList().apply { this[index] = value }
}
