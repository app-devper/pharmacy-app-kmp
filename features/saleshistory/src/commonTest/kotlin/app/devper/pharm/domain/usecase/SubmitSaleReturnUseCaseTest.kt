@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.sales.SubmitSaleReturnUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.sales.ReturnLineParam
import app.devper.pharm.domain.param.sales.SubmitReturnParam
import app.devper.pharm.domain.repository.FakeSaleHistoryRepository
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun testDispatchers() = UnconfinedTestDispatcher().let { d ->
    AppDispatchers(main = d, io = d, default = d)
}

class SubmitSaleReturnUseCaseTest {

    @Test
    fun forwards_param_to_repository_when_valid() = runTest {
        val repo = FakeSaleHistoryRepository()
        val param = SubmitReturnParam(
            saleId = "s1",
            reason = "ลูกค้าคืน",
            items = listOf(ReturnLineParam(saleItemId = "i1", qty = 2)),
        )

        val result = SubmitSaleReturnUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isSuccess)
        assertEquals(param, repo.lastSubmitReturn?.copy(clientRequestId = null))
        assertNotNull(repo.lastSubmitReturn?.clientRequestId)
    }

    private fun returnOf(qty: Int) = SubmitReturnParam(
        saleId = "s1",
        reason = "ลูกค้าคืน",
        items = listOf(ReturnLineParam(saleItemId = "i1", qty = qty)),
    )

    @Test
    fun retry_of_the_same_return_reuses_its_request_id() = runTest {
        val repo = FakeSaleHistoryRepository().apply { failNextSubmit = true }
        val useCase = SubmitSaleReturnUseCase(repo, testDispatchers())

        assertTrue(useCase(returnOf(1)).isFailure)
        assertTrue(useCase(returnOf(1)).isSuccess)

        assertEquals(2, repo.submittedRequestIds.size)
        assertEquals(repo.submittedRequestIds[0], repo.submittedRequestIds[1])
    }

    @Test
    fun a_changed_or_new_return_gets_a_new_request_id() = runTest {
        val repo = FakeSaleHistoryRepository().apply { failNextSubmit = true }
        val useCase = SubmitSaleReturnUseCase(repo, testDispatchers())

        assertTrue(useCase(returnOf(1)).isFailure)
        assertTrue(useCase(returnOf(2)).isSuccess) // cashier changed the quantity
        assertTrue(useCase(returnOf(2)).isSuccess) // a second, separate return

        assertEquals(3, repo.submittedRequestIds.toSet().size)
    }

    @Test
    fun blank_reason_fails_validation() = runTest {
        val repo = FakeSaleHistoryRepository()
        val param = SubmitReturnParam(
            saleId = "s1",
            reason = "   ",
            items = listOf(ReturnLineParam(saleItemId = "i1", qty = 1)),
        )

        val result = SubmitSaleReturnUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
        assertNull(repo.lastSubmitReturn)
    }

    @Test
    fun zero_qty_items_are_filtered_out_before_dispatch() = runTest {
        val repo = FakeSaleHistoryRepository()
        val param = SubmitReturnParam(
            saleId = "s1",
            reason = "ลูกค้าคืน",
            items = listOf(
                ReturnLineParam(saleItemId = "i1", qty = 0),
                ReturnLineParam(saleItemId = "i2", qty = 3),
                ReturnLineParam(saleItemId = "i3", qty = 0),
            ),
        )

        SubmitSaleReturnUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        val dispatched = repo.lastSubmitReturn!!
        assertEquals(1, dispatched.items.size)
        assertEquals("i2", dispatched.items[0].saleItemId)
        assertEquals(3, dispatched.items[0].qty)
    }

    @Test
    fun all_zero_qty_fails_validation() = runTest {
        val repo = FakeSaleHistoryRepository()
        val param = SubmitReturnParam(
            saleId = "s1",
            reason = "ลูกค้าคืน",
            items = listOf(
                ReturnLineParam(saleItemId = "i1", qty = 0),
                ReturnLineParam(saleItemId = "i2", qty = 0),
            ),
        )

        val result = SubmitSaleReturnUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
        assertNull(repo.lastSubmitReturn)
    }

    @Test
    fun empty_items_fails_validation() = runTest {
        val repo = FakeSaleHistoryRepository()
        val param = SubmitReturnParam(saleId = "s1", reason = "x", items = emptyList())

        val result = SubmitSaleReturnUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeSaleHistoryRepository(submitThrowsOn = "s1")
        val param = SubmitReturnParam(
            saleId = "s1",
            reason = "ลูกค้าคืน",
            items = listOf(ReturnLineParam(saleItemId = "i1", qty = 1)),
        )

        val result = SubmitSaleReturnUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
    }
}
