package app.devper.pharm.presentation.suppliers

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.repository.FakeSupplierRepository
import app.devper.pharm.domain.usecase.AddSupplierUseCase
import app.devper.pharm.domain.usecase.GetSuppliersUseCase
import app.devper.pharm.domain.usecase.UpdateSupplierUseCase
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
class SupplierFormViewModelTest {

    private fun supplier(
        id: String = "s1",
        name: String = "ACME Pharma",
    ) = Supplier(
        id = id,
        name = name,
        contactName = "John",
        phone = "0812345678",
        address = "123 BKK",
        taxId = "0105550000001",
        notes = "preferred",
    )

    private data class Bundle(
        val vm: SupplierFormViewModel,
        val repo: FakeSupplierRepository,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        repo: FakeSupplierRepository = FakeSupplierRepository(),
    ): Bundle {
        val vm = SupplierFormViewModel(
            getSuppliers = GetSuppliersUseCase(repo, dispatchers),
            addSupplier = AddSupplierUseCase(repo, dispatchers),
            updateSupplier = UpdateSupplierUseCase(repo, dispatchers),
        )
        return Bundle(vm, repo)
    }

    @Test
    fun add_mode_starts_with_empty_form() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(SupplierFormMode.Add)
        advanceUntilIdle()
        val s = vm.state.value
        assertTrue(s.mode is SupplierFormMode.Add)
        assertEquals("", s.form.name)
        assertEquals("เพิ่มผู้จัดจำหน่าย", s.titleLabel)
        assertFalse(s.canSubmit)
    }

    @Test
    fun edit_mode_hydrates_form_from_supplier() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeSupplierRepository(seed = listOf(supplier())))
        vm.init(SupplierFormMode.Edit("s1"))
        advanceUntilIdle()
        val f = vm.state.value.form
        assertEquals("ACME Pharma", f.name)
        assertEquals("John", f.contactName)
        assertEquals("0812345678", f.phone)
        assertEquals("0105550000001", f.taxId)
        assertEquals("preferred", f.notes)
        assertEquals("แก้ไขผู้จัดจำหน่าย", vm.state.value.titleLabel)
    }

    @Test
    fun edit_mode_supplier_not_found_surfaces_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeSupplierRepository(seed = emptyList()))
        vm.init(SupplierFormMode.Edit("missing"))
        advanceUntilIdle()
        assertEquals("ไม่พบผู้จัดจำหน่าย", vm.state.value.error)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun canSubmit_false_until_name_filled() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(SupplierFormMode.Add)
        advanceUntilIdle()
        assertFalse(vm.state.value.canSubmit)
        vm.onName("ACME")
        assertTrue(vm.state.value.canSubmit)
        vm.onName("  ")
        assertFalse(vm.state.value.canSubmit)
    }

    @Test
    fun submit_add_happy_path_trims_inputs_and_marks_saved() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        vm.init(SupplierFormMode.Add)
        advanceUntilIdle()
        vm.onName("  ACME Pharma  ")
        vm.onContactName(" John ")
        vm.onPhone("0812345678")
        vm.onAddress("123 BKK")
        vm.onTaxId("0105550000001")
        vm.onNotes("notes")
        vm.submit()
        advanceUntilIdle()
        val p = repo.lastAdd
        assertNotNull(p)
        assertEquals("ACME Pharma", p.name)
        assertEquals("John", p.contactName)
        assertEquals("0812345678", p.phone)
        assertEquals("notes", p.notes)
        assertTrue(vm.state.value.saved)
        assertFalse(vm.state.value.saving)
    }

    @Test
    fun submit_edit_happy_path_calls_update_with_id() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers, FakeSupplierRepository(seed = listOf(supplier(id = "s99"))))
        vm.init(SupplierFormMode.Edit("s99"))
        advanceUntilIdle()
        vm.onName("Renamed")
        vm.submit()
        advanceUntilIdle()
        val p = repo.lastUpdate
        assertNotNull(p)
        assertEquals("s99", p.id)
        assertEquals("Renamed", p.name)
        assertTrue(vm.state.value.saved)
    }

    @Test
    fun submit_failure_surfaces_error_does_not_mark_saved() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeSupplierRepository(addThrowsOn = "BAD"))
        vm.init(SupplierFormMode.Add)
        advanceUntilIdle()
        vm.onName("BAD")
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saved)
        assertNotNull(vm.state.value.error)
        assertFalse(vm.state.value.saving)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(SupplierFormMode.Edit("missing"))
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        vm.dismissError()
        assertNull(vm.state.value.error)
    }
}
