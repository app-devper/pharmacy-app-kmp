@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.purchasing.AddPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.purchasing.ConfirmPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.purchasing.DeletePurchaseOrderUseCase
import app.devper.pharm.domain.usecase.purchasing.GetPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.purchasing.GetPurchaseOrdersUseCase
import app.devper.pharm.domain.usecase.purchasing.UpdatePurchaseOrderUseCase

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.domain.param.AddPurchaseOrderParam
import app.devper.pharm.domain.param.PurchaseOrderItemInput
import app.devper.pharm.domain.param.UpdatePurchaseOrderParam
import app.devper.pharm.domain.repository.FakePurchaseOrderRepository
import app.devper.pharm.domain.testDispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun itemInput() = PurchaseOrderItemInput(
    drugId = "d1", drugName = "Drug A", lotNumber = "LOT-1",
    expiryDate = LocalDate(2027, 6, 30),
    qty = Quantity(10), costPrice = Money(2.0), sellPrice = Money(5.0),
)

private fun addParam(supplier: String = "ACME") = AddPurchaseOrderParam(
    supplier = supplier, invoiceNo = "INV-1",
    receiveDate = LocalDate(2026, 5, 14), notes = "",
    items = listOf(itemInput()),
)

private fun seededPO(id: String, supplier: String = "ACME") = PurchaseOrder(
    id = id, docNo = "PO-$id", supplier = supplier, invoiceNo = "INV-1",
    receiveDate = LocalDate(2026, 5, 14), items = emptyList(), itemCount = 0,
    totalCost = Money.Zero, status = PurchaseOrderStatus.Draft,
    notes = "", createdAt = LocalDateTime(2026, 5, 14, 0, 0), confirmedAt = null,
)

class AddPurchaseOrderUseCaseTest {

    @Test
    fun forwards_param_and_returns_synthesised_po() = runTest {
        val repo = FakePurchaseOrderRepository()
        val param = addParam(supplier = "ACME")

        val po = AddPurchaseOrderUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastAdd)
        assertEquals("ACME", po.supplier)
        assertEquals(1, po.items.size)
        assertEquals(Money(20.0), po.totalCost)
    }

    @Test
    fun backend_rejection_wraps_in_result_failure() = runTest {
        val repo = FakePurchaseOrderRepository(addThrowsOn = "BadSupplier")
        val param = addParam(supplier = "BadSupplier")

        val result = AddPurchaseOrderUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
        assertNull(repo.lastAdd)
    }
}

class UpdatePurchaseOrderUseCaseTest {

    @Test
    fun forwards_param_to_repository() = runTest {
        val repo = FakePurchaseOrderRepository()
        val param = UpdatePurchaseOrderParam(
            id = "po-1", supplier = "ACME 2", invoiceNo = "INV-2",
            receiveDate = LocalDate(2026, 5, 15), notes = "edited",
            items = listOf(itemInput()),
        )

        val result = UpdatePurchaseOrderUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastUpdate)
        assertEquals("ACME 2", result.supplier)
    }
}

class DeletePurchaseOrderUseCaseTest {

    @Test
    fun forwards_id_to_repository() = runTest {
        val repo = FakePurchaseOrderRepository()

        DeletePurchaseOrderUseCase(repo, testDispatchers()).invoke("po-1").getOrThrow()

        assertEquals("po-1", repo.lastDelete)
    }
}

class GetPurchaseOrderUseCaseTest {

    @Test
    fun returns_seeded_po() = runTest {
        val po = seededPO("po-1")
        val repo = FakePurchaseOrderRepository(seed = mapOf("po-1" to po))

        val result = GetPurchaseOrderUseCase(repo, testDispatchers()).invoke("po-1")

        assertEquals(po, result.getOrThrow())
    }

    @Test
    fun missing_id_returns_failure() = runTest {
        val repo = FakePurchaseOrderRepository()

        val result = GetPurchaseOrderUseCase(repo, testDispatchers()).invoke("nonexistent")

        assertTrue(result.isFailure)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakePurchaseOrderRepository(getThrows = true)

        val result = GetPurchaseOrderUseCase(repo, testDispatchers()).invoke("po-1")

        assertTrue(result.isFailure)
    }
}

class GetPurchaseOrdersUseCaseTest {

    @Test
    fun returns_seeded_summary_list() = runTest {
        val summary = PurchaseOrderSummary(
            id = "po-1", docNo = "PO-1", supplier = "ACME", invoiceNo = "INV-1",
            receiveDate = LocalDate(2026, 5, 14), itemCount = 0,
            totalCost = Money(100.0), status = PurchaseOrderStatus.Draft,
            notes = "", createdAt = null, confirmedAt = null,
        )
        val repo = FakePurchaseOrderRepository(listSeed = listOf(summary))

        val result = GetPurchaseOrdersUseCase(repo, testDispatchers()).invoke()

        assertEquals(listOf(summary), result.getOrThrow())
    }

    @Test
    fun empty_seed_returns_empty_list() = runTest {
        val repo = FakePurchaseOrderRepository()

        val result = GetPurchaseOrdersUseCase(repo, testDispatchers()).invoke()

        assertEquals(emptyList(), result.getOrThrow())
    }
}

class ConfirmPurchaseOrderUseCaseTest {

    @Test
    fun confirms_seeded_po_and_marks_confirmed() = runTest {
        val po = seededPO("po-1")
        val repo = FakePurchaseOrderRepository(seed = mapOf("po-1" to po))

        val result = ConfirmPurchaseOrderUseCase(repo, testDispatchers()).invoke("po-1").getOrThrow()

        assertEquals("po-1", repo.lastConfirm)
        assertEquals(PurchaseOrderStatus.Confirmed, result.status)
    }

    @Test
    fun missing_id_fails_with_repository_exception() = runTest {
        val repo = FakePurchaseOrderRepository()

        val result = ConfirmPurchaseOrderUseCase(repo, testDispatchers()).invoke("missing")

        assertTrue(result.isFailure)
    }
}
