package app.devper.pharm.presentation.imports

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.domain.repository.FakePurchaseOrderRepository
import app.devper.pharm.domain.usecase.purchasing.ConfirmPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.purchasing.DeletePurchaseOrderUseCase
import app.devper.pharm.domain.usecase.purchasing.GetPurchaseOrdersUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ImportsListViewModelTest {

    private fun summary(id: String, status: PurchaseOrderStatus = PurchaseOrderStatus.Draft) =
        PurchaseOrderSummary(
            id = id, docNo = "GR-$id", supplier = "s", invoiceNo = "INV-$id", receiveDate = kotlinx.datetime.LocalDate.parse("2026-06-01"),
            itemCount = 1, totalCost = Money(100.0), status = status, notes = "", createdAt = kotlinx.datetime.LocalDateTime.parse("2026-06-01T09:00:00"),
            confirmedAt = null,
        )

    private fun vm(repo: FakePurchaseOrderRepository, d: app.devper.pharm.common.AppDispatchers) =
        ImportsListViewModel(
            GetPurchaseOrdersUseCase(repo, d),
            ConfirmPurchaseOrderUseCase(repo, d),
            DeletePurchaseOrderUseCase(repo, d),
        )

    @Test
    fun init_loads_orders() = runVmTest { d ->
        val model = vm(FakePurchaseOrderRepository(listSeed = listOf(summary("a"), summary("b"))), d)
        advanceUntilIdle()
        assertEquals(2, model.state.value.orders.size)
        assertFalse(model.state.value.loading)
        assertNull(model.state.value.errorState)
    }

    @Test
    fun request_confirm_ignored_for_non_draft() = runVmTest { d ->
        val model = vm(FakePurchaseOrderRepository(), d)
        advanceUntilIdle()
        model.requestConfirm(summary("a", status = PurchaseOrderStatus.Confirmed))
        assertNull(model.state.value.pendingConfirm)
    }
}
