package app.devper.pharm.presentation.suppliers

import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.repository.FakeSupplierRepository
import app.devper.pharm.domain.usecase.suppliers.DeleteSupplierUseCase
import app.devper.pharm.domain.usecase.suppliers.GetSuppliersUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import app.devper.pharm.common.error.CommonUiStateError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SuppliersListViewModelTest {

    private fun supplier(id: String) = Supplier(
        id = id, name = "Supplier $id", contactName = "", phone = "", address = "", taxId = "", notes = "",
    )

    private fun vm(repo: FakeSupplierRepository, d: app.devper.pharm.common.AppDispatchers) =
        SuppliersListViewModel(GetSuppliersUseCase(repo, d), DeleteSupplierUseCase(repo, d))

    @Test
    fun init_loads_suppliers() = runVmTest { d ->
        val model = vm(FakeSupplierRepository(seed = listOf(supplier("a"), supplier("b"))), d)
        advanceUntilIdle()
        assertEquals(2, model.state.value.suppliers.size)
        assertFalse(model.state.value.loading)
        assertNull(model.state.value.errorState)
    }

    @Test
    fun delete_confirmed_removes_supplier() = runVmTest { d ->
        val model = vm(FakeSupplierRepository(seed = listOf(supplier("a"), supplier("b"))), d)
        advanceUntilIdle()
        model.confirmDelete(supplier("a"))
        model.deleteConfirmed()
        advanceUntilIdle()
        assertEquals(listOf("b"), model.state.value.suppliers.map { it.id })
        assertNull(model.state.value.pendingDelete)
    }

    @Test
    fun load_failure_sets_error_state_and_clears_loading() = runVmTest { d ->
        val model = vm(FakeSupplierRepository(listThrows = true), d)
        advanceUntilIdle()
        assertIs<CommonUiStateError.LoadFailed>(model.state.value.errorState)
        assertFalse(model.state.value.loading)
    }

    @Test
    fun confirm_delete_sets_pending_delete() = runVmTest { d ->
        val model = vm(FakeSupplierRepository(seed = listOf(supplier("x"))), d)
        advanceUntilIdle()
        model.confirmDelete(supplier("x"))
        assertEquals("x", model.state.value.pendingDelete?.id)
    }

    @Test
    fun cancel_delete_clears_pending_delete() = runVmTest { d ->
        val model = vm(FakeSupplierRepository(seed = listOf(supplier("x"))), d)
        advanceUntilIdle()
        model.confirmDelete(supplier("x"))
        assertNotNull(model.state.value.pendingDelete)
        model.cancelDelete()
        assertNull(model.state.value.pendingDelete)
    }

    @Test
    fun delete_failure_sets_delete_error() = runVmTest { d ->
        val model = vm(FakeSupplierRepository(seed = listOf(supplier("a")), deleteThrows = true), d)
        advanceUntilIdle()
        model.confirmDelete(supplier("a"))
        model.deleteConfirmed()
        advanceUntilIdle()
        assertIs<CommonUiStateError.DeleteFailed>(model.state.value.errorState)
        assertFalse(model.state.value.deleting)
    }

    @Test
    fun query_filters_suppliers_by_name() = runVmTest { d ->
        val model = vm(FakeSupplierRepository(seed = listOf(supplier("alpha"), supplier("beta"))), d)
        advanceUntilIdle()
        model.onQueryChange("Supplier alpha")
        val filtered = model.state.value.filtered
        assertEquals(1, filtered.size)
        assertTrue(filtered.first().id == "alpha")
    }
}
