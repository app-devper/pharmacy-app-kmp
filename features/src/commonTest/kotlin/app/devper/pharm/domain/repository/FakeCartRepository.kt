package app.devper.pharm.domain.repository

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeCartRepository(
    initialItems: List<CartLine> = emptyList(),
    initialCustomer: Customer? = null,
    initialDiscount: CartDiscount = CartDiscount.None,
    initialTier: String = Tier.Retail,
    initialReceived: String = "",
    initialReceipt: Sale? = null,
    initialParkedSlots: List<ParkedCart?> = List(PARK_SLOT_COUNT) { null },
) : CartRepository {

    private val itemsState = MutableStateFlow(initialItems)
    override val items: StateFlow<List<CartLine>> = itemsState.asStateFlow()

    private val customerState = MutableStateFlow(initialCustomer)
    override val selectedCustomer: StateFlow<Customer?> = customerState.asStateFlow()

    private val discountState = MutableStateFlow(initialDiscount)
    override val cartDiscount: StateFlow<CartDiscount> = discountState.asStateFlow()

    private val tierState = MutableStateFlow(initialTier)
    override val activeTier: StateFlow<String> = tierState.asStateFlow()

    private val receivedState = MutableStateFlow(initialReceived)
    override val cashReceived: StateFlow<String> = receivedState.asStateFlow()

    private val receiptState = MutableStateFlow(initialReceipt)
    override val lastReceipt: StateFlow<Sale?> = receiptState.asStateFlow()

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
        itemsState.value = itemsState.value.filterNot { it.key == key }
    }

    override fun selectCustomer(customer: Customer) {
        lastSelectCustomer = customer
        customerState.value = customer
    }

    override fun clearCustomer() {
        clearCustomerCalled = true
        customerState.value = null
    }

    override fun setCartDiscount(discount: CartDiscount) {
        lastSetCartDiscount = discount
        discountState.value = discount
    }

    override fun setCashReceived(value: String) {
        lastSetCashReceived = value
        receivedState.value = value
    }

    override fun commitReceipt(sale: Sale) {
        lastCommitReceipt = sale
        receiptState.value = sale
        itemsState.value = emptyList()
        receivedState.value = ""
    }

    override fun dismissReceipt() {
        dismissReceiptCalled = true
        receiptState.value = null
    }

    override fun clear() {
        clearCalled = true
        itemsState.value = emptyList()
        customerState.value = null
        discountState.value = CartDiscount.None
        receivedState.value = ""
    }

    override fun parkCart(slot: Int) {
        lastParkSlot = slot
        if (itemsState.value.isEmpty()) return
        parkClockMs += 1000L
        val snapshot = ParkedCart(
            items = itemsState.value,
            customer = customerState.value,
            cartDiscount = discountState.value,
            activeTier = tierState.value,
            cashReceived = receivedState.value,
            parkedAt = parkClockMs,
        )
        parkedState.value = parkedState.value.mapIndexed { i, existing ->
            if (i == slot) snapshot else existing
        }

        itemsState.value = emptyList()
        customerState.value = null
        discountState.value = CartDiscount.None
        receivedState.value = ""
    }

    override fun restoreCart(slot: Int) {
        lastRestoreSlot = slot
        val parked = parkedState.value.getOrNull(slot) ?: return
        itemsState.value = parked.items
        customerState.value = parked.customer
        discountState.value = parked.cartDiscount
        tierState.value = parked.activeTier
        receivedState.value = parked.cashReceived
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

    fun pushItems(items: List<CartLine>) { itemsState.value = items }
    fun pushCashReceived(value: String) { receivedState.value = value }
    fun pushCustomer(customer: Customer?) { customerState.value = customer }
    fun pushReceipt(sale: Sale?) { receiptState.value = sale }
    fun pushTier(tier: String) { tierState.value = tier }
}
