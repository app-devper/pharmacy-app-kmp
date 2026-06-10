package app.devper.pharm.presentation.customers

import app.devper.pharm.presentation.customers.exception.CustomerDetailUiStateError

import app.devper.pharm.common.value.Money

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.repository.FakeCustomerRepository
import app.devper.pharm.domain.usecase.customers.GetCustomerSalesUseCase
import app.devper.pharm.domain.usecase.customers.GetCustomersUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CustomerDetailViewModelTest {

    private fun customer(id: String = "c1", name: String = "John") = Customer(
        id = id,
        name = name,
        phone = "0812345678",
        priceTier = "",
        allergyNote = null,
    )

    private fun saleSummary(id: String = "s1") = SaleSummary(
        id = id,
        billNo = "INV-$id",
        customerName = "John",
        total = Money(100.0),
        discount = Money(0.0),
        soldAt = kotlinx.datetime.LocalDateTime.parse("2026-05-14T10:00:00"),
        voided = false,
    )

    private data class Bundle(
        val vm: CustomerDetailViewModel,
        val repo: FakeCustomerRepository,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        repo: FakeCustomerRepository = FakeCustomerRepository(),
    ): Bundle {
        val vm = CustomerDetailViewModel(
            getCustomers = GetCustomersUseCase(repo, dispatchers),
            getCustomerSales = GetCustomerSalesUseCase(repo, dispatchers),
        )
        return Bundle(vm, repo)
    }

    @Test
    fun load_happy_path_populates_customer_and_sales() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(
            dispatchers,
            FakeCustomerRepository(
                seed = listOf(customer("c1"), customer("c2")),
                salesBy = mapOf("c1" to listOf(saleSummary("s1"), saleSummary("s2"))),
            ),
        )
        advanceUntilIdle()
        vm.load("c1")
        advanceUntilIdle()
        val s = vm.state.value
        val customer = assertNotNull(s.customer)
        assertEquals("c1", customer.id)
        assertEquals(2, s.sales.size)
        assertEquals("c1", repo.lastSalesQuery)
        assertFalse(s.loading)
        assertNull(s.errorState)
    }

    @Test
    fun load_clears_previous_state_before_fetching() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            FakeCustomerRepository(
                seed = listOf(customer("c1"), customer("c2")),
                salesBy = mapOf(
                    "c1" to listOf(saleSummary("s1")),
                    "c2" to listOf(saleSummary("s2"), saleSummary("s3")),
                ),
            ),
        )
        advanceUntilIdle()
        vm.load("c1")
        advanceUntilIdle()
        assertEquals("c1", vm.state.value.customer?.id)
        assertEquals(1, vm.state.value.sales.size)
        vm.load("c2")
        advanceUntilIdle()
        assertEquals("c2", vm.state.value.customer?.id)
        assertEquals(2, vm.state.value.sales.size)
    }

    @Test
    fun load_customer_not_found_sets_error_but_continues_to_load_sales() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            FakeCustomerRepository(
                seed = listOf(customer("c1")),
                salesBy = mapOf("missing" to emptyList()),
            ),
        )
        advanceUntilIdle()
        vm.load("missing")
        advanceUntilIdle()
        val s = vm.state.value
        assertNull(s.customer)
        assertIs<CustomerDetailUiStateError.CustomerNotFound>(s.errorState)
        assertFalse(s.customerLoading)
        assertFalse(s.salesLoading)
    }

    @Test
    fun load_customers_failure_surfaces_error_and_clears_customer_loading() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeCustomerRepository(listThrows = true))
        advanceUntilIdle()
        vm.load("c1")
        advanceUntilIdle()
        assertNotNull(vm.state.value.errorState)
        assertFalse(vm.state.value.customerLoading)
    }

    @Test
    fun load_sales_failure_surfaces_error_keeps_customer_loaded() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            FakeCustomerRepository(
                seed = listOf(customer("c1")),
                salesThrowsOn = "c1",
            ),
        )
        advanceUntilIdle()
        vm.load("c1")
        advanceUntilIdle()
        assertEquals("c1", vm.state.value.customer?.id)
        assertNotNull(vm.state.value.errorState)
        assertFalse(vm.state.value.salesLoading)
        assertTrue(vm.state.value.sales.isEmpty())
    }

    @Test
    fun loading_derived_from_customer_or_sales_loading() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeCustomerRepository(seed = listOf(customer())))
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        vm.load("c1")
        assertTrue(vm.state.value.loading)
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeCustomerRepository(listThrows = true))
        advanceUntilIdle()
        vm.load("c1")
        advanceUntilIdle()
        assertNotNull(vm.state.value.errorState)
        vm.dismissError()
        assertNull(vm.state.value.errorState)
    }
}
