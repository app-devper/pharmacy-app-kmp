package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.repository.FakeCustomerRepository
import app.devper.pharm.domain.usecase.customers.GetCustomersUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class CustomersListViewModelTest {

    private fun customer(id: String) = Customer(
        id = id, name = "Customer $id", phone = null, priceTier = "", allergyNote = null,
    )

    @Test
    fun init_loads_customers() = runVmTest { d ->
        val repo = FakeCustomerRepository(seed = listOf(customer("a"), customer("b")))
        val vm = CustomersListViewModel(GetCustomersUseCase(repo, d))
        advanceUntilIdle()
        assertEquals(2, vm.state.value.customers.size)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun query_change_updates_state() = runVmTest { d ->
        val vm = CustomersListViewModel(GetCustomersUseCase(FakeCustomerRepository(), d))
        advanceUntilIdle()
        vm.onQueryChange("สมศรี")
        assertEquals("สมศรี", vm.state.value.query)
    }
}
