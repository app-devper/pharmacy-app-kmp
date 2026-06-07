package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.repository.FakeCustomerRepository
import app.devper.pharm.domain.usecase.AddCustomerUseCase
import app.devper.pharm.domain.usecase.GetCustomersUseCase
import app.devper.pharm.domain.usecase.UpdateCustomerUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CustomerFormViewModelTest {

    private fun newVm(
        dispatchers: app.devper.pharm.common.AppDispatchers,
        repo: FakeCustomerRepository = FakeCustomerRepository(),
    ): Pair<CustomerFormViewModel, FakeCustomerRepository> {
        val vm = CustomerFormViewModel(
            getCustomers = GetCustomersUseCase(repo, dispatchers),
            addCustomer = AddCustomerUseCase(repo, dispatchers),
            updateCustomer = UpdateCustomerUseCase(repo, dispatchers),
        )
        return vm to repo
    }

    @Test
    fun init_add_mode_starts_empty() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(CustomerFormMode.Add)
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals(CustomerFormMode.Add, s.mode)
        assertEquals("", s.form.name)
        assertEquals("", s.form.phone)
        assertFalse(s.loading)
        assertFalse(s.canSubmit)
    }

    @Test
    fun init_edit_mode_hydrates_from_repo() = runVmTest { dispatchers ->
        val seedCustomer = Customer(
            id = "c1",
            name = "สมศรี ใจดี",
            phone = "0812345678",
            priceTier = "regular",
            allergyNote = "แพ้ Penicillin",
        )
        val (vm, _) = newVm(dispatchers, FakeCustomerRepository(seed = listOf(seedCustomer)))
        vm.init(CustomerFormMode.Edit("c1"))
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals("สมศรี ใจดี", s.form.name)
        assertEquals("0812345678", s.form.phone)
        assertEquals("แพ้ Penicillin", s.form.allergyNote)
        assertEquals("regular", s.form.priceTier)
        assertFalse(s.loading)
        assertTrue(s.canSubmit)
    }

    @Test
    fun init_edit_mode_unknown_id_surfaces_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeCustomerRepository(seed = emptyList()))
        vm.init(CustomerFormMode.Edit("does-not-exist"))
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals("ไม่พบลูกค้า", s.error)
        assertFalse(s.loading)
    }

    @Test
    fun canSubmit_requires_non_blank_name() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(CustomerFormMode.Add)
        vm.onName("   ")
        assertFalse(vm.state.value.canSubmit)
        vm.onName("Foo")
        assertTrue(vm.state.value.canSubmit)
    }

    @Test
    fun submit_in_add_mode_calls_addCustomer_with_typed_param() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        vm.init(CustomerFormMode.Add)
        vm.onName("สมชาย")
        vm.onPhone("0890001234")
        vm.onPriceTier("wholesale")
        vm.submit()
        advanceUntilIdle()
        val captured = repo.lastAdd
        assertNotNull(captured)
        assertEquals("สมชาย", captured.name)
        assertEquals("0890001234", captured.phone)
        assertEquals("wholesale", captured.priceTier)
        assertTrue(vm.state.value.saved)
        assertFalse(vm.state.value.saving)
    }

    @Test
    fun submit_in_edit_mode_calls_updateCustomer_with_id() = runVmTest { dispatchers ->
        val seed = Customer(id = "c1", name = "เก่า", phone = null, priceTier = "", allergyNote = null)
        val (vm, repo) = newVm(dispatchers, FakeCustomerRepository(seed = listOf(seed)))
        vm.init(CustomerFormMode.Edit("c1"))
        advanceUntilIdle()
        vm.onName("ใหม่")
        vm.submit()
        advanceUntilIdle()
        val captured = repo.lastUpdate
        assertNotNull(captured)
        assertEquals("c1", repo.lastUpdateId)
        assertEquals("ใหม่", captured.name)
        assertTrue(vm.state.value.saved)
    }

    @Test
    fun submit_failure_routes_to_error_not_saved() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeCustomerRepository(addThrowsOn = "Boom"))
        vm.init(CustomerFormMode.Add)
        vm.onName("Boom")
        vm.submit()
        advanceUntilIdle()
        val s = vm.state.value
        assertNotNull(s.error)
        assertFalse(s.saved)
        assertFalse(s.saving)
    }

    @Test
    fun submit_short_circuits_when_canSubmit_is_false() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        vm.init(CustomerFormMode.Add)

        vm.submit()
        advanceUntilIdle()
        assertNull(repo.lastAdd)
        assertFalse(vm.state.value.saving)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeCustomerRepository(addThrowsOn = "Boom"))
        vm.init(CustomerFormMode.Add)
        vm.onName("Boom")
        vm.submit()
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        vm.dismissError()
        assertNull(vm.state.value.error)
    }

    @Test
    fun resetSaved_clears_saved_flag_for_consecutive_submits() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(CustomerFormMode.Add)
        vm.onName("Foo")
        vm.submit()
        advanceUntilIdle()
        assertTrue(vm.state.value.saved)
        vm.resetSaved()
        assertFalse(vm.state.value.saved)
    }
}
