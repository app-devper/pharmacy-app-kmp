package app.devper.pharm.presentation.suppliers

import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.repository.FakeSupplierRepository
import app.devper.pharm.domain.usecase.suppliers.DeleteSupplierUseCase
import app.devper.pharm.domain.usecase.suppliers.GetSuppliersUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

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
}
