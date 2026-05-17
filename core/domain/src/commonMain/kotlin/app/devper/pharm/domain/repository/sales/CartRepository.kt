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
import kotlinx.coroutines.flow.StateFlow

interface CartRepository {
    val items: StateFlow<List<CartLine>>
    val selectedCustomer: StateFlow<Customer?>
    val cartDiscount: StateFlow<CartDiscount>
    val activeTier: StateFlow<String>
    val cashReceived: StateFlow<String>
    val lastReceipt: StateFlow<Sale?>

    val parkedSlots: StateFlow<List<ParkedCart?>>

    fun add(param: AddCartItemParam)

    fun setQty(param: SetCartQtyParam)

    fun setLineDiscount(param: SetLineDiscountParam)

    fun remove(key: CartLineKey)

    fun selectCustomer(customer: Customer)
    fun clearCustomer()

    fun setCartDiscount(discount: CartDiscount)
    fun setCashReceived(value: String)

    fun commitReceipt(sale: Sale)
    fun dismissReceipt()

    fun clear()

    fun parkCart(slot: Int)

    fun restoreCart(slot: Int)

    fun discardSlot(slot: Int)
}

const val PARK_SLOT_COUNT = 5
