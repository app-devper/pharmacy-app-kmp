package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.repository.FakeCartRepository
import app.devper.pharm.domain.repository.FakeCustomerRepository
import app.devper.pharm.domain.usecase.ClearCustomerUseCase
import app.devper.pharm.domain.usecase.GetCustomersUseCase
import app.devper.pharm.domain.usecase.SelectCustomerUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CustomerPickerViewModelTest {

    private fun customer(id: String, name: String) =
        Customer(id = id, name = name, phone = null, priceTier = "", allergyNote = null)

    private data class Bundle(
        val vm: CustomerPickerViewModel,
        val cart: FakeCartRepository,
        val repo: FakeCustomerRepository,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        cart: FakeCartRepository = FakeCartRepository(),
        repo: FakeCustomerRepository = FakeCustomerRepository(),
    ): Bundle {
        val vm = CustomerPickerViewModel(
            getCustomers = GetCustomersUseCase(repo, dispatchers),
            selectCustomer = SelectCustomerUseCase(cart),
            clearCustomer = ClearCustomerUseCase(cart),
        )
        return Bundle(vm, cart, repo)
    }

    @Test
    fun open_first_time_loads_list_and_clears_loading() = runVmTest { dispatchers ->
        val seed = listOf(customer("c1", "Alice"), customer("c2", "Bob"))
        val (vm) = newVm(dispatchers, repo = FakeCustomerRepository(seed = seed))
        advanceUntilIdle()
        vm.open()
        assertTrue(vm.state.value.open)

        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        assertEquals(2, vm.state.value.customers.size)
        assertEquals("Alice", vm.state.value.customers[0].name)
    }

    @Test
    fun open_second_time_skips_reload_when_customers_already_populated() = runVmTest { dispatchers ->
        val seed = listOf(customer("c1", "Alice"))

        val countingRepo = object : app.devper.pharm.domain.repository.CustomerRepository {
            var listCalls = 0; private set
            override suspend fun list(): List<Customer> {
                listCalls++
                return seed
            }
            override suspend fun add(param: app.devper.pharm.domain.param.AddCustomerParam) =
                throw NotImplementedError("not under test")
            override suspend fun update(param: app.devper.pharm.domain.param.UpdateCustomerParam) =
                throw NotImplementedError("not under test")
            override suspend fun getCustomerSales(customerId: String) =
                throw NotImplementedError("not under test")
        }
        val vm = CustomerPickerViewModel(
            getCustomers = GetCustomersUseCase(countingRepo, dispatchers),
            selectCustomer = SelectCustomerUseCase(FakeCartRepository()),
            clearCustomer = ClearCustomerUseCase(FakeCartRepository()),
        )
        advanceUntilIdle()
        vm.open()
        advanceUntilIdle()
        assertEquals(1, countingRepo.listCalls)
        vm.close()
        vm.open()
        advanceUntilIdle()
        assertEquals(1, countingRepo.listCalls)
    }

    @Test
    fun pick_selects_customer_on_cart_and_closes_sheet() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers)
        advanceUntilIdle()
        vm.open()
        val charlie = customer("c3", "Charlie")
        vm.pick(charlie)
        advanceUntilIdle()
        assertEquals(charlie, cart.lastSelectCustomer)
        assertEquals(charlie, cart.state.value.active.customer)
        assertFalse(vm.state.value.open)
    }

    @Test
    fun clear_drops_customer_from_cart_without_touching_sheet() = runVmTest { dispatchers ->
        val cart = FakeCartRepository(initialCustomer = customer("c1", "Alice"))
        val (vm, _) = newVm(dispatchers, cart = cart)
        advanceUntilIdle()
        vm.open()
        vm.clear()
        advanceUntilIdle()
        assertTrue(cart.clearCustomerCalled)
        assertNull(cart.state.value.active.customer)

        assertTrue(vm.state.value.open)
    }

    @Test
    fun open_failure_routes_to_error_and_clears_loading() = runVmTest { dispatchers ->
        val throwingRepo = object : app.devper.pharm.domain.repository.CustomerRepository {
            override suspend fun list(): List<Customer> = throw RuntimeException("offline")
            override suspend fun add(param: app.devper.pharm.domain.param.AddCustomerParam) =
                throw NotImplementedError()
            override suspend fun update(param: app.devper.pharm.domain.param.UpdateCustomerParam) =
                throw NotImplementedError()
            override suspend fun getCustomerSales(customerId: String) =
                throw NotImplementedError()
        }
        val vm = CustomerPickerViewModel(
            getCustomers = GetCustomersUseCase(throwingRepo, dispatchers),
            selectCustomer = SelectCustomerUseCase(FakeCartRepository()),
            clearCustomer = ClearCustomerUseCase(FakeCartRepository()),
        )
        advanceUntilIdle()
        vm.open()
        advanceUntilIdle()
        assertEquals("offline", vm.state.value.error)
        assertFalse(vm.state.value.loading)

        vm.dismissError()
        assertNull(vm.state.value.error)
    }
}
