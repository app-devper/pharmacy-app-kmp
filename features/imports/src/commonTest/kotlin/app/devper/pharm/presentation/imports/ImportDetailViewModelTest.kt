package app.devper.pharm.presentation.imports

import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.repository.FakePurchaseOrderRepository
import app.devper.pharm.domain.usecase.ConfirmPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.DeletePurchaseOrderUseCase
import app.devper.pharm.domain.usecase.GetPurchaseOrderUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ImportDetailViewModelTest {

    private fun po(id: String) = PurchaseOrder(
        id = id, docNo = "GR-$id", supplier = "s", invoiceNo = "INV-$id", receiveDate = kotlinx.datetime.LocalDate.parse("2026-06-01"),
        items = emptyList(), itemCount = 0, totalCost = 0.0, status = PurchaseOrderStatus.Draft,
        notes = "", createdAt = kotlinx.datetime.LocalDateTime.parse("2026-06-01T09:00:00"), confirmedAt = null,
    )

    private fun vm(repo: FakePurchaseOrderRepository, d: app.devper.pharm.common.AppDispatchers) =
        ImportDetailViewModel(
            GetPurchaseOrderUseCase(repo, d),
            ConfirmPurchaseOrderUseCase(repo, d),
            DeletePurchaseOrderUseCase(repo, d),
        )

    @Test
    fun init_loads_purchase_order() = runVmTest { d ->
        val model = vm(FakePurchaseOrderRepository(seed = mapOf("po1" to po("po1"))), d)
        model.init("po1")
        advanceUntilIdle()
        assertEquals("po1", model.state.value.po?.id)
        assertFalse(model.state.value.loading)
        assertNull(model.state.value.error)
    }

    @Test
    fun dialogs_toggle_state() = runVmTest { d ->
        val model = vm(FakePurchaseOrderRepository(), d)
        model.askConfirm()
        assertEquals(true, model.state.value.confirmDialog)
        model.cancelConfirm()
        assertEquals(false, model.state.value.confirmDialog)
    }
}
