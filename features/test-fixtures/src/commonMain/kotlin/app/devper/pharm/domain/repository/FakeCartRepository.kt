package app.devper.pharm.domain.repository

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
import app.devper.pharm.domain.extension.Tier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeCartRepository(
    initialItems: List<CartLine> = emptyList(),
    initialCustomer: Customer? = null,
    initialDiscount: CartDiscount = CartDiscount.None,
    initialTier: String = Tier.Retail,
    initialReceived: String = "",
    initialReceipt: Sale? = null,
    initialParkedSlots: List<ParkedCart?> = List(PARK_SLOT_COUNT) { null },
) : CartRepository {

    private val internalState = MutableStateFlow(
        CartState(
            active = ActiveCart(
                items = initialItems,
                customer = initialCustomer,
                cartDiscount = initialDiscount,
                activeTier = initialTier,
                cashReceived = initialReceived,
            ),
            lastReceipt = initialReceipt,
        ),
    )
    override val state: StateFlow<CartState> = internalState.asStateFlow()

    private val parkedState = MutableStateFlow(initialParkedSlots)
    override val parkedSlots: StateFlow<List<ParkedCart?>> = parkedState.asStateFlow()

    var lastAdd: AddCartItemParam? = null; private set
    var lastSetQty: SetCartQtyParam? = null; private set
    var lastSetLineDiscount: SetLineDiscountParam? = null; private set
    var lastSetCartDiscount: CartDiscount? = null; private set
    var lastSetCashReceived: String? = null; private set
    var lastRemove: CartLineKey? = null; private set
    var lastSelectCustomer: Customer? = null; private set
    var clearCustomerCalled: Boolean = false; private set
    var lastCommitReceipt: Sale? = null; private set
    var dismissReceiptCalled: Boolean = false; private set
    var clearCalled: Boolean = false; private set
    var lastParkSlot: Int? = null; private set
    var lastRestoreSlot: Int? = null; private set
    var lastDiscardSlot: Int? = null; private set

    private var parkClockMs: Long = 0L

    override fun add(param: AddCartItemParam) {
        lastAdd = param
    }

    override fun setQty(param: SetCartQtyParam) {
        lastSetQty = param
    }

    override fun setLineDiscount(param: SetLineDiscountParam) {
        lastSetLineDiscount = param
    }

    override fun remove(key: CartLineKey) {
        lastRemove = key
        mutateActive { it.copy(items = it.items.filterNot { line -> line.key == key }) }
    }

    override fun selectCustomer(customer: Customer) {
        lastSelectCustomer = customer
        mutateActive { it.copy(customer = customer) }
    }

    override fun clearCustomer() {
        clearCustomerCalled = true
        mutateActive { it.copy(customer = null) }
    }

    override fun setCartDiscount(discount: CartDiscount) {
        lastSetCartDiscount = discount
        mutateActive { it.copy(cartDiscount = discount) }
    }

    override fun setCashReceived(value: String) {
        lastSetCashReceived = value
        mutateActive { it.copy(cashReceived = value) }
    }

    override fun commitReceipt(sale: Sale) {
        lastCommitReceipt = sale
        internalState.value = CartState(
            active = internalState.value.active.copy(items = emptyList(), cashReceived = ""),
            lastReceipt = sale,
        )
    }

    override fun dismissReceipt() {
        dismissReceiptCalled = true
        internalState.update { it.copy(lastReceipt = null) }
    }

    override fun clear() {
        clearCalled = true
        mutateActive {
            it.copy(items = emptyList(), customer = null, cartDiscount = CartDiscount.None, cashReceived = "")
        }
    }

    override fun parkCart(slot: Int) {
        lastParkSlot = slot
        val snapshot = internalState.value.active
        if (snapshot.items.isEmpty()) return
        parkClockMs += 1000L
        val parked = ParkedCart(
            items = snapshot.items,
            customer = snapshot.customer,
            cartDiscount = snapshot.cartDiscount,
            activeTier = snapshot.activeTier,
            cashReceived = snapshot.cashReceived,
            parkedAt = parkClockMs,
        )
        parkedState.value = parkedState.value.mapIndexed { i, existing ->
            if (i == slot) parked else existing
        }
        mutateActive { ActiveCart.Empty }
    }

    override fun restoreCart(slot: Int) {
        lastRestoreSlot = slot
        val parked = parkedState.value.getOrNull(slot) ?: return
        internalState.value = CartState(
            active = ActiveCart(
                items = parked.items,
                customer = parked.customer,
                cartDiscount = parked.cartDiscount,
                activeTier = parked.activeTier,
                cashReceived = parked.cashReceived,
            ),
            lastReceipt = null,
        )
        parkedState.value = parkedState.value.mapIndexed { i, existing ->
            if (i == slot) null else existing
        }
    }

    override fun discardSlot(slot: Int) {
        lastDiscardSlot = slot
        parkedState.value = parkedState.value.mapIndexed { i, existing ->
            if (i == slot) null else existing
        }
    }

    fun pushItems(items: List<CartLine>) { mutateActive { it.copy(items = items) } }
    fun pushCashReceived(value: String) { mutateActive { it.copy(cashReceived = value) } }
    fun pushCustomer(customer: Customer?) { mutateActive { it.copy(customer = customer) } }
    fun pushReceipt(sale: Sale?) { internalState.update { it.copy(lastReceipt = sale) } }
    fun pushTier(tier: String) { mutateActive { it.copy(activeTier = tier) } }

    private inline fun mutateActive(crossinline transform: (ActiveCart) -> ActiveCart) {
        internalState.update { it.copy(active = transform(it.active)) }
    }
}
