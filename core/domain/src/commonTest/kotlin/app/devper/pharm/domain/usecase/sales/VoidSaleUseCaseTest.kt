@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.sales.VoidSaleUseCase

import app.devper.pharm.domain.param.VoidSaleParam
import app.devper.pharm.domain.repository.FakeSaleRepository
import app.devper.pharm.domain.testDispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VoidSaleUseCaseTest {

    @Test
    fun forwards_param_to_repository_when_reason_provided() = runTest {
        val repo = FakeSaleRepository()
        val param = VoidSaleParam(saleId = "s1", reason = "ออกใบเสร็จผิด")

        val result = VoidSaleUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isSuccess)
        assertEquals(param, repo.lastVoid)
    }

    @Test
    fun blank_reason_fails_validation() = runTest {
        val repo = FakeSaleRepository()
        val param = VoidSaleParam(saleId = "s1", reason = "   ")

        val result = VoidSaleUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
        assertNull(repo.lastVoid)
    }

    @Test
    fun empty_reason_fails_validation() = runTest {
        val repo = FakeSaleRepository()
        val param = VoidSaleParam(saleId = "s1", reason = "")

        val result = VoidSaleUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
        assertFalse(repo.lastVoid != null)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeSaleRepository(voidThrows = RuntimeException("conflict"))
        val param = VoidSaleParam(saleId = "s1", reason = "ผิด")

        val result = VoidSaleUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
    }
}
