@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.sales.CheckoutUseCase

import app.devper.pharm.domain.validation.SaleValidationError

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.domain.testDispatchers
import app.devper.pharm.domain.model.ActiveCart
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.model.CartState
import app.devper.pharm.domain.model.CheckoutFailure
import app.devper.pharm.domain.model.CheckoutOutcome
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.sales.AddCartItemParam
import app.devper.pharm.domain.param.sales.CheckoutParam
import app.devper.pharm.domain.param.sales.SetCartQtyParam
import app.devper.pharm.domain.param.sales.SetLineDiscountParam
import app.devper.pharm.domain.param.sales.VoidSaleParam
import app.devper.pharm.domain.repository.sales.CartRepository
import app.devper.pharm.domain.repository.sales.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CheckoutUseCaseTest {

    private fun drug(id: String, stock: Int) = Drug(
        id = id, name = "Drug $id", genericName = null, type = null, strength = null,
        barcode = null, sellPrice = Money(10.0), costPrice = Money(0.0), stock = Quantity(stock), minStock = Quantity(0),
        unit = "เม็ด", regNo = null,
    )

    private fun useCase(active: ActiveCart, sales: FakeSales) =
        CheckoutUseCase(FakeCart(active), sales, testDispatchers())

    private fun cart(vararg lines: CartLine, received: String = "100") =
        ActiveCart(items = lines.toList(), cashReceived = received)

    @Test
    fun empty_cart_fails_with_validation() = runTest {
        val result = useCase(cart(), FakeSales()).invoke(received = Money(0.0))
        assertTrue(result.isFailure)
        val failure = result.exceptionOrNull()
        assertTrue(failure is CheckoutFailure)
        assertTrue(failure.cause is SaleValidationError.EmptyCart)
    }

    @Test
    fun shortfall_without_oversell_routes_to_confirm() = runTest {
        val line = CartLine(drug = drug("a", stock = 1), qty = 3)
        val outcome = useCase(cart(line), FakeSales()).invoke(received = Money(100.0)).getOrThrow()
        assertTrue(outcome is CheckoutOutcome.NeedsOversellConfirm)
        val shortfalls = outcome.shortfalls
        assertEquals(1, shortfalls.size)
        assertEquals(3, shortfalls[0].asked)
        assertEquals(1, shortfalls[0].available)
    }

    @Test
    fun shortfall_aggregates_across_lines_of_same_drug() = runTest {
        val d = drug("a", stock = 4)
        val outcome = useCase(
            cart(CartLine(drug = d, qty = 3), CartLine(drug = d, qty = 3, tier = "x")),
            FakeSales(),
        ).invoke(received = Money(100.0)).getOrThrow()
        val shortfalls = (outcome as CheckoutOutcome.NeedsOversellConfirm).shortfalls
        assertEquals(1, shortfalls.size)
        assertEquals(6, shortfalls[0].asked)
        assertEquals(4, shortfalls[0].available)
    }

    @Test
    fun allow_oversell_checks_out_and_flags_only_oversold_lines() = runTest {
        val sales = FakeSales()
        val short = CartLine(drug = drug("short", stock = 1), qty = 3)
        val ok = CartLine(drug = drug("ok", stock = 50), qty = 2)
        val outcome = useCase(cart(short, ok), sales).invoke(received = Money(100.0), allowOversell = true).getOrThrow()
        assertTrue(outcome is CheckoutOutcome.Success)
        val param = sales.lastParam!!
        assertTrue(param.items.first { it.drugId == "short" }.allowOversell)
        assertEquals(false, param.items.first { it.drugId == "ok" }.allowOversell)
    }

    @Test
    fun line_without_alt_unit_sends_unit_factor_one_not_zero() = runTest {
        val sales = FakeSales()
        val line = CartLine(drug = drug("a", stock = 10), qty = 2)
        useCase(cart(line), sales).invoke(received = Money(100.0)).getOrThrow()
        val itemParam = sales.lastParam!!.items.single()
        assertEquals(1, itemParam.unitFactor)
        assertEquals("", itemParam.unit)
    }

    @Test
    fun line_with_alt_unit_sends_alt_unit_name_and_factor() = runTest {
        val sales = FakeSales()
        val alt = app.devper.pharm.domain.model.AltUnit(
            name = "กล่อง", factor = 10, sellPrice = Money(100.0),
        )
        val line = CartLine(drug = drug("a", stock = 100), qty = 10, selectedUnit = alt)
        useCase(cart(line), sales).invoke(received = Money(200.0)).getOrThrow()
        val itemParam = sales.lastParam!!.items.single()
        assertEquals(10, itemParam.unitFactor)
        assertEquals("กล่อง", itemParam.unit)
    }

    @Test
    fun success_commits_receipt_to_cart() = runTest {
        val sales = FakeSales()
        val fakeCart = FakeCart(cart(CartLine(drug = drug("a", stock = 10), qty = 2)))
        val outcome = CheckoutUseCase(fakeCart, sales, testDispatchers()).invoke(received = Money(100.0)).getOrThrow()
        assertTrue(outcome is CheckoutOutcome.Success)
        assertEquals(sales.sale.id, fakeCart.committed?.id)
    }

    @Test
    fun checkout_throwing_wraps_in_checkout_failure_with_payload() = runTest {
        val boom = RuntimeException("network down")
        val sales = FakeSales(failWith = boom)
        val result = CheckoutUseCase(
            FakeCart(cart(CartLine(drug = drug("a", stock = 10), qty = 1))),
            sales,
            testDispatchers(),
        ).invoke(received = Money(100.0), clientRequestId = "req-1")
        assertTrue(result.isFailure)
        val failure = result.exceptionOrNull() as CheckoutFailure
        assertEquals(boom, failure.cause)
        assertEquals("serialized", failure.serializedRequest)
        assertEquals("req-1", failure.clientRequestId)
    }
}

private class FakeCart(active: ActiveCart) : CartRepository {
    private val _state = MutableStateFlow(CartState(active = active))
    override val state: StateFlow<CartState> = _state.asStateFlow()
    override val parkedSlots: StateFlow<List<ParkedCart?>> = MutableStateFlow<List<ParkedCart?>>(emptyList()).asStateFlow()

    var committed: Sale? = null
        private set

    override fun commitReceipt(sale: Sale) { committed = sale }

    override fun add(param: AddCartItemParam) {}
    override fun setQty(param: SetCartQtyParam) {}
    override fun setLineDiscount(param: SetLineDiscountParam) {}
    override fun remove(key: CartLineKey) {}
    override fun selectCustomer(customer: Customer) {}
    override fun clearCustomer() {}
    override fun setCartDiscount(discount: CartDiscount) {}
    override fun setCashReceived(value: String) {}
    override fun dismissReceipt() {}
    override fun clear() {}
    override fun parkCart(slot: Int) {}
    override fun restoreCart(slot: Int) {}
    override fun discardSlot(slot: Int) {}
}

private class FakeSales(
    val sale: Sale = Sale(
        id = "s1", billNo = "B1", total = Money(20.0), change = Money(80.0), discount = Money(0.0), stockUpdates = emptyList(),
    ),
    private val failWith: Throwable? = null,
) : SaleRepository {
    var lastParam: CheckoutParam? = null
        private set

    override suspend fun checkout(param: CheckoutParam): Sale {
        lastParam = param
        failWith?.let { throw it }
        return sale
    }

    override suspend fun void(param: VoidSaleParam) {}
    override fun serializeCheckout(param: CheckoutParam): String = "serialized"
    override suspend fun replayCheckout(payloadJson: String): Sale = sale
}
