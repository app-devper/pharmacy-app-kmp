@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.sales.SubmitSaleReturnUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.ReturnLineParam
import app.devper.pharm.domain.param.SubmitReturnParam
import app.devper.pharm.domain.repository.FakeSaleHistoryRepository
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
        assertEquals(param, repo.lastSubmitReturn)
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
