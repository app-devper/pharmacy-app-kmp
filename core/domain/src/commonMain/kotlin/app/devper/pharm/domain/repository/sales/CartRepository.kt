package app.devper.pharm.domain.repository.sales

import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.model.CartState
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.sales.AddCartItemParam
import app.devper.pharm.domain.param.sales.SetCartQtyParam
import app.devper.pharm.domain.param.sales.SetLineDiscountParam
import kotlinx.coroutines.flow.StateFlow

interface CartRepository {
    val state: StateFlow<CartState>
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
