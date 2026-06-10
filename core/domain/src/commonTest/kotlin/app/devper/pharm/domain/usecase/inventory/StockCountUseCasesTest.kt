@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.inventory.CreateStockCountUseCase
import app.devper.pharm.domain.usecase.inventory.GetStockCountsUseCase

import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.domain.model.StockCountLine
import app.devper.pharm.domain.param.CreateStockCountParam
import app.devper.pharm.domain.param.StockCountInputLine
import app.devper.pharm.domain.repository.FakeStockCountsRepository
import app.devper.pharm.domain.testDispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun seededCount(id: String) = StockCount(
    id = id, countNo = "CN-$id", note = "",
    items = listOf(StockCountLine(drugId = "d1", drugName = "A", unit = "", systemStock = 5, counted = 5, delta = 0)),
    createdAt = LocalDateTime(2026, 5, 14, 0, 0),
)

class CreateStockCountUseCaseTest {

    @Test
    fun forwards_param_and_returns_persisted_count() = runTest {
        val repo = FakeStockCountsRepository()
        val param = CreateStockCountParam(
            note = "month-end",
            items = listOf(
                StockCountInputLine(drugId = "d1", counted = 10),
                StockCountInputLine(drugId = "d2", counted = 7),
            ),
        )

        val result = CreateStockCountUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastAdd)
        assertEquals(2, result.items.size)
        assertEquals(10, result.items[0].counted)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeStockCountsRepository(addThrows = true)
        val param = CreateStockCountParam(items = emptyList())

        val result = CreateStockCountUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
        assertNull(repo.lastAdd)
    }
}

class GetStockCountsUseCaseTest {

    @Test
    fun direct_param_forwards_limit_to_repository() = runTest {
        val seed = listOf(seededCount("a"), seededCount("b"), seededCount("c"))
        val repo = FakeStockCountsRepository(seed = seed)

        val result = GetStockCountsUseCase(repo, testDispatchers()).invoke(2).getOrThrow()

        assertEquals(2, result.size)
        assertEquals(1, repo.listCallCount)
    }

    @Test
    fun convenience_invoke_uses_default_limit_20() = runTest {
        val seed = List(25) { seededCount("c-$it") }
        val repo = FakeStockCountsRepository(seed = seed)

        val result = GetStockCountsUseCase(repo, testDispatchers()).invoke().getOrThrow()

        assertEquals(20, result.size)
    }
}
