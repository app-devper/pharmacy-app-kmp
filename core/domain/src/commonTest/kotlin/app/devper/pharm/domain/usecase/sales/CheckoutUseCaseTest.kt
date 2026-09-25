@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.repository.FakeOfflineSaleQueue

import app.devper.pharm.domain.usecase.sales.CheckoutUseCase

import app.devper.pharm.domain.validation.SaleValidationError

import app.devper.pharm.common.IdentityUnavailableException
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
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CheckoutUseCaseTest {

    private fun drug(id: String, stock: Int) = Drug(
        id = id, name = "Drug $id", genericName = null, type = null, strength = null,
        barcode = null, sellPrice = Money(10.0), costPrice = Money(0.0), stock = Quantity(stock), minStock = Quantity(0),
        unit = "เม็ด", regNo = null,
    )

    private fun useCase(active: ActiveCart, sales: FakeSales) =
        CheckoutUseCase(FakeCart(active), sales, FakeOfflineSaleQueue(), testDispatchers())

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
        val checkout = useCase(cart(short, ok), sales)
        assertTrue(checkout.invoke(received = Money(100.0)).getOrThrow() is CheckoutOutcome.NeedsOversellConfirm)
        val outcome = checkout.invoke(received = Money(100.0), allowOversell = true).getOrThrow()
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
        val outcome = CheckoutUseCase(fakeCart, sales, FakeOfflineSaleQueue(), testDispatchers()).invoke(received = Money(100.0)).getOrThrow()
        assertTrue(outcome is CheckoutOutcome.Success)
        assertEquals(sales.sale.id, fakeCart.committed?.id)
    }

    @Test
    fun checkout_throwing_wraps_in_checkout_failure_with_payload() = runTest {
        val boom = RuntimeException("server rejected checkout")
        val sales = FakeSales(failWith = boom)
        val result = CheckoutUseCase(
            FakeCart(cart(CartLine(drug = drug("a", stock = 10), qty = 1))),
            sales,
            FakeOfflineSaleQueue(),
            testDispatchers(),
        ).invoke(received = Money(100.0))
        assertTrue(result.isFailure)
        val failure = result.exceptionOrNull() as CheckoutFailure
        assertEquals(boom, failure.cause)
        assertNotNull(sales.lastParam?.clientRequestId)
    }
    @Test
    fun network_failure_clears_cart_only_after_queue_accepts_payload() = runTest {
        val active = cart(CartLine(drug = drug("a", stock = 10), qty = 1))
        val cart = FakeCart(active)
        val queue = FakeOfflineSaleQueue()
        val sales = FakeSales(failWith = RuntimeException("Failed to connect to host"))
        val result = CheckoutUseCase(cart, sales, queue, testDispatchers())
            .invoke(Money(100.0)).getOrThrow()
        assertEquals(CheckoutOutcome.OfflineSaved, result)
        assertEquals(sales.lastParam?.clientRequestId, queue.lastEnqueue?.clientRequestId)
        assertEquals("serialized", queue.lastEnqueue?.payloadJson)
        assertTrue(cart.state.value.active.items.isEmpty())
        assertNull(cart.committed)
    }

    @Test
    fun identity_outage_keeps_the_sale_pending_like_a_network_failure() = runTest {
        val cart = FakeCart(cart(CartLine(drug = drug("a", stock = 10), qty = 1)))
        val queue = FakeOfflineSaleQueue()
        val sales = FakeSales(failWith = IdentityUnavailableException())
        val result = CheckoutUseCase(cart, sales, queue, testDispatchers())
            .invoke(Money(100.0)).getOrThrow()
        assertEquals(CheckoutOutcome.OfflineSaved, result)
        assertEquals(sales.lastParam?.clientRequestId, queue.lastEnqueue?.clientRequestId)
        assertTrue(cart.state.value.active.items.isEmpty())
        assertNull(cart.committed)
    }

    @Test
    fun queue_failure_preserves_cart_and_surfaces_failure() = runTest {
        val active = cart(CartLine(drug = drug("a", stock = 10), qty = 1))
        val cart = FakeCart(active)
        val error = RuntimeException("storage full")
        val result = CheckoutUseCase(cart, FakeSales(failWith = RuntimeException("Failed to connect to host")), FakeOfflineSaleQueue(enqueueThrows = error), testDispatchers())
            .invoke(Money(100.0))
        assertEquals(error, result.exceptionOrNull())
        assertEquals(active, cart.state.value.active)
        assertNull(cart.committed)
    }

    @Test
    fun network_and_queue_failure_retry_reuses_request_id() = runTest {
        val cart = FakeCart(cart(CartLine(drug = drug("a", stock = 10), qty = 1)))
        val sales = FakeSales(failWith = RuntimeException("Failed to connect to host"))
        val queueFailure = RuntimeException("storage full")
        val checkout = CheckoutUseCase(cart, sales, FakeOfflineSaleQueue(enqueueThrows = queueFailure), testDispatchers())

        assertEquals(queueFailure, checkout.invoke(Money(100.0)).exceptionOrNull())
        val firstRequestId = assertNotNull(sales.lastParam?.clientRequestId)
        assertEquals(queueFailure, checkout.invoke(Money(100.0)).exceptionOrNull())

        assertEquals(firstRequestId, sales.lastParam?.clientRequestId)
        assertTrue(cart.state.value.active.items.isNotEmpty())
    }

    @Test
    fun changed_cart_after_oversell_prompt_is_not_sold_on_confirmation() = runTest {
        val initial = cart(CartLine(drug = drug("a", stock = 1), qty = 3))
        val fakeCart = FakeCart(initial)
        val sales = FakeSales()
        val checkout = CheckoutUseCase(fakeCart, sales, FakeOfflineSaleQueue(), testDispatchers())

        assertTrue(checkout.invoke(Money(100.0)).getOrThrow() is CheckoutOutcome.NeedsOversellConfirm)
        fakeCart.replaceActive(cart(CartLine(drug = drug("a", stock = 1), qty = 4)))
        val outcome = checkout.invoke(Money(100.0), allowOversell = true).getOrThrow()

        assertEquals(CheckoutOutcome.CartChanged, outcome)
        assertNull(sales.lastParam)
        assertNull(fakeCart.committed)
    }

    @Test
    fun failed_attempt_reuses_request_id_for_same_sale_payload() = runTest {
        val sales = FakeSales(failWith = RuntimeException("validation: missing field"))
        val checkout = useCase(cart(CartLine(drug = drug("a", stock = 10), qty = 1)), sales)

        checkout.invoke(Money(100.0))
        val firstRequestId = assertNotNull(sales.lastParam?.clientRequestId)
        checkout.invoke(Money(100.0))

        assertEquals(firstRequestId, sales.lastParam?.clientRequestId)
    }

    @Test
    fun changed_sale_payload_gets_new_request_id() = runTest {
        val sales = FakeSales(failWith = RuntimeException("validation: missing field"))
        val checkout = useCase(cart(CartLine(drug = drug("a", stock = 10), qty = 1)), sales)

        checkout.invoke(Money(100.0))
        val firstRequestId = assertNotNull(sales.lastParam?.clientRequestId)
        checkout.invoke(Money(101.0))

        assertNotEquals(firstRequestId, sales.lastParam?.clientRequestId)
    }

    @Test
    fun successful_sale_clears_request_identity_for_next_sale() = runTest {
        val sales = FakeSales()
        val checkout = useCase(cart(CartLine(drug = drug("a", stock = 10), qty = 1)), sales)

        checkout.invoke(Money(100.0))
        val firstRequestId = assertNotNull(sales.lastParam?.clientRequestId)
        checkout.invoke(Money(100.0))

        assertNotEquals(firstRequestId, sales.lastParam?.clientRequestId)
    }

}

private class FakeCart(active: ActiveCart) : CartRepository {
    private val _state = MutableStateFlow(CartState(active = active))
    override val state: StateFlow<CartState> = _state.asStateFlow()
    override val parkedSlots: StateFlow<List<ParkedCart?>> = MutableStateFlow<List<ParkedCart?>>(emptyList()).asStateFlow()

    var committed: Sale? = null
        private set

    fun replaceActive(active: ActiveCart) { _state.value = CartState(active = active) }

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
    override fun clear() { _state.value = CartState() }
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
