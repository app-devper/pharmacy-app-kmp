@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.customers.AddCustomerUseCase
import app.devper.pharm.domain.usecase.customers.GetCustomerSalesUseCase
import app.devper.pharm.domain.usecase.customers.GetCustomersUseCase
import app.devper.pharm.domain.usecase.customers.UpdateCustomerUseCase
import app.devper.pharm.domain.usecase.inventory.AddDrugUseCase
import app.devper.pharm.domain.usecase.inventory.GetDrugsUseCase
import app.devper.pharm.domain.usecase.inventory.UpdateDrugUseCase
import app.devper.pharm.domain.usecase.offlinesync.EnqueueOfflineSaleUseCase
import app.devper.pharm.domain.usecase.offlinesync.MarkOfflineSaleSyncedUseCase
import app.devper.pharm.domain.usecase.suppliers.AddSupplierUseCase
import app.devper.pharm.domain.usecase.suppliers.DeleteSupplierUseCase
import app.devper.pharm.domain.usecase.suppliers.GetSuppliersUseCase
import app.devper.pharm.domain.usecase.suppliers.UpdateSupplierUseCase

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.param.inventory.AddDrugParam
import app.devper.pharm.domain.param.customers.CustomerInput
import app.devper.pharm.domain.param.offlinesync.EnqueueOfflineSaleParam
import app.devper.pharm.domain.param.suppliers.SupplierInput
import app.devper.pharm.domain.param.customers.UpdateCustomerParam
import app.devper.pharm.domain.param.inventory.UpdateDrugParam
import app.devper.pharm.domain.param.suppliers.UpdateSupplierParam
import app.devper.pharm.domain.repository.FakeCustomerRepository
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.repository.FakeOfflineSaleQueue
import app.devper.pharm.domain.repository.FakeSupplierRepository
import app.devper.pharm.domain.testDispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun customer(id: String = "c1", name: String = "John") =
    Customer(id = id, name = name, phone = null, priceTier = "", allergyNote = null)

private fun supplier(id: String = "s1", name: String = "ACME") =
    Supplier(id = id, name = name, contactName = "", phone = "", address = "", taxId = "", notes = "")

private fun saleSummary(id: String, billNo: String, customerName: String = "x") = SaleSummary(
    id = id, billNo = billNo, customerName = customerName,
    total = Money.Zero, discount = Money.Zero, soldAt = null, voided = false,
)

private fun addDrugParam(name: String = "Paracetamol", barcode: String = "B1") = AddDrugParam(
    name = name, barcode = barcode,
    sellPrice = Money(2.0), costPrice = Money(1.0),
    stock = Quantity.Zero, minStock = Quantity.Zero,
)

class AddCustomerUseCaseTest {

    @Test
    fun forwards_input_to_repository_and_returns_added_customer() = runTest {
        val repo = FakeCustomerRepository()
        val input = CustomerInput(name = "John", phone = "0812345678")

        val result = AddCustomerUseCase(repo, testDispatchers()).invoke(input)

        assertEquals(input, repo.lastAdd)
        assertEquals("John", result.getOrThrow().name)
    }

    @Test
    fun backend_rejection_wraps_in_result_failure() = runTest {
        val repo = FakeCustomerRepository(addThrowsOn = "Bad")
        val input = CustomerInput(name = "Bad")

        val result = AddCustomerUseCase(repo, testDispatchers()).invoke(input)

        assertTrue(result.isFailure)
        assertNull(repo.lastAdd)
    }
}

class UpdateCustomerUseCaseTest {

    @Test
    fun forwards_id_and_input_to_repository() = runTest {
        val repo = FakeCustomerRepository()
        val param = UpdateCustomerParam("c1", CustomerInput(name = "Jane"))

        val result = UpdateCustomerUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isSuccess)
        assertEquals("c1", repo.lastUpdateId)
        assertEquals(param.input, repo.lastUpdate)
    }
}

class GetCustomersUseCaseTest {

    @Test
    fun returns_seeded_customer_list() = runTest {
        val seed = listOf(customer("c1"), customer("c2", "Jane"))
        val repo = FakeCustomerRepository(seed = seed)

        val result = GetCustomersUseCase(repo, testDispatchers()).invoke()

        assertEquals(seed, result.getOrThrow())
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeCustomerRepository(listThrows = true)

        val result = GetCustomersUseCase(repo, testDispatchers()).invoke()

        assertTrue(result.isFailure)
    }
}

class GetCustomerSalesUseCaseTest {

    @Test
    fun forwards_customer_id_and_returns_sales_list() = runTest {
        val sales = listOf(saleSummary("s1", "B-1"))
        val repo = FakeCustomerRepository(salesBy = mapOf("c1" to sales))

        val result = GetCustomerSalesUseCase(repo, testDispatchers()).invoke("c1")

        assertEquals(sales, result.getOrThrow())
        assertEquals("c1", repo.lastSalesQuery)
    }

    @Test
    fun unknown_customer_returns_empty_list() = runTest {
        val repo = FakeCustomerRepository()

        val result = GetCustomerSalesUseCase(repo, testDispatchers()).invoke("c-missing")

        assertEquals(emptyList(), result.getOrThrow())
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeCustomerRepository(salesThrowsOn = "c-bad")

        val result = GetCustomerSalesUseCase(repo, testDispatchers()).invoke("c-bad")

        assertTrue(result.isFailure)
    }
}

class AddSupplierUseCaseTest {

    @Test
    fun forwards_input_and_returns_added_supplier() = runTest {
        val repo = FakeSupplierRepository()
        val input = SupplierInput(name = "ACME", phone = "02-123-4567")

        val result = AddSupplierUseCase(repo, testDispatchers()).invoke(input)

        assertEquals(input, repo.lastAdd)
        assertEquals("ACME", result.getOrThrow().name)
    }

    @Test
    fun backend_rejection_wraps_in_result_failure() = runTest {
        val repo = FakeSupplierRepository(addThrowsOn = "Bad")
        val input = SupplierInput(name = "Bad")

        val result = AddSupplierUseCase(repo, testDispatchers()).invoke(input)

        assertTrue(result.isFailure)
    }
}

class UpdateSupplierUseCaseTest {

    @Test
    fun forwards_id_and_input_to_repository() = runTest {
        val repo = FakeSupplierRepository()
        val param = UpdateSupplierParam("s1", SupplierInput(name = "ACME 2"))

        UpdateSupplierUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals("s1", repo.lastUpdateId)
        assertEquals(param.input, repo.lastUpdate)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeSupplierRepository(updateThrowsOn = "s1")
        val param = UpdateSupplierParam("s1", SupplierInput(name = "X"))

        val result = UpdateSupplierUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
    }
}

class DeleteSupplierUseCaseTest {

    @Test
    fun forwards_id_to_repository() = runTest {
        val repo = FakeSupplierRepository()

        DeleteSupplierUseCase(repo, testDispatchers()).invoke("s1").getOrThrow()

        assertEquals("s1", repo.lastDelete)
    }
}

class GetSuppliersUseCaseTest {

    @Test
    fun returns_seeded_supplier_list() = runTest {
        val seed = listOf(supplier("s1"), supplier("s2", "BetaCo"))
        val repo = FakeSupplierRepository(seed = seed)

        val result = GetSuppliersUseCase(repo, testDispatchers()).invoke()

        assertEquals(seed, result.getOrThrow())
        assertEquals(1, repo.listCallCount)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeSupplierRepository(listThrows = true)

        val result = GetSuppliersUseCase(repo, testDispatchers()).invoke()

        assertTrue(result.isFailure)
    }
}

class AddDrugUseCaseTest {

    @Test
    fun forwards_param_and_returns_added_drug() = runTest {
        val repo = FakeDrugRepository()
        val param = addDrugParam(name = "Paracetamol", barcode = "BC-1")

        val result = AddDrugUseCase(repo, testDispatchers()).invoke(param)

        assertEquals(param, repo.lastAdd)
        assertEquals("Paracetamol", result.getOrThrow().name)
    }

    @Test
    fun backend_rejection_wraps_in_result_failure() = runTest {
        val repo = FakeDrugRepository(addThrowsOn = "BAD-BARCODE")
        val param = addDrugParam(barcode = "BAD-BARCODE")

        val result = AddDrugUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
    }
}

class UpdateDrugUseCaseTest {

    @Test
    fun forwards_param_to_repository() = runTest {
        val repo = FakeDrugRepository()
        val param = UpdateDrugParam(
            id = "d1", name = "Paracetamol",
            sellPrice = Money(3.0), costPrice = Money(1.5),
            minStock = Quantity(10),
        )

        UpdateDrugUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastUpdate)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeDrugRepository(updateThrowsOn = "d1")
        val param = UpdateDrugParam(
            id = "d1", name = "x",
            sellPrice = Money.Zero, costPrice = Money.Zero,
            minStock = Quantity.Zero,
        )

        val result = UpdateDrugUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
    }
}

class GetDrugsUseCaseTest {

    @Test
    fun returns_seeded_drug_list() = runTest {
        val repo = FakeDrugRepository(seed = emptyList())

        val result = GetDrugsUseCase(repo, testDispatchers()).invoke()

        assertEquals(emptyList(), result.getOrThrow())
        assertEquals(1, repo.listCallCount)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeDrugRepository(listThrows = true)

        val result = GetDrugsUseCase(repo, testDispatchers()).invoke()

        assertTrue(result.isFailure)
    }
}

class EnqueueOfflineSaleUseCaseTest {

    @Test
    fun forwards_param_and_returns_queue_id() {
        val queue = FakeOfflineSaleQueue()
        val param = EnqueueOfflineSaleParam(clientRequestId = "req-1", payloadJson = "{...}")

        val result = EnqueueOfflineSaleUseCase(queue).invoke(param)

        assertEquals(param, queue.lastEnqueue)
        assertNotNull(result.getOrNull())
        assertEquals(1, queue.pending.value.size)
    }

    @Test
    fun convenience_invoke_builds_param() {
        val queue = FakeOfflineSaleQueue()

        val result = EnqueueOfflineSaleUseCase(queue).invoke(
            clientRequestId = "req-2", payloadJson = "[]",
        )

        assertEquals("req-2", queue.lastEnqueue?.clientRequestId)
        assertEquals("[]", queue.lastEnqueue?.payloadJson)
        assertTrue(result.isSuccess)
    }
}

class MarkOfflineSaleSyncedUseCaseTest {

    @Test
    fun forwards_id_and_removes_from_queue() = runTest {
        val queue = FakeOfflineSaleQueue()
        queue.enqueue(EnqueueOfflineSaleParam(clientRequestId = "req-1", payloadJson = "{}"))
        val id = queue.pending.value.single().id

        MarkOfflineSaleSyncedUseCase(queue, testDispatchers()).invoke(id).getOrThrow()

        assertEquals(id, queue.lastMarkSynced)
        assertTrue(queue.pending.value.isEmpty())
    }

    @Test
    fun queue_failure_wraps_in_result() = runTest {
        val queue = FakeOfflineSaleQueue(markSyncedThrows = RuntimeException("storage failed"))

        val result = MarkOfflineSaleSyncedUseCase(queue, testDispatchers()).invoke("any-id")

        assertTrue(result.isFailure)
    }
}
