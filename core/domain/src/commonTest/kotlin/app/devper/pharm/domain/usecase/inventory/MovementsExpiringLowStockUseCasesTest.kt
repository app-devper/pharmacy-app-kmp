@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.inventory.GetExpiringLotsUseCase
import app.devper.pharm.domain.usecase.inventory.GetLowStockDrugsUseCase
import app.devper.pharm.domain.usecase.inventory.WriteoffLotsUseCase
import app.devper.pharm.domain.usecase.reports.GetMovementsUseCase

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.model.StockMovementsPage
import app.devper.pharm.domain.model.WriteoffResult
import app.devper.pharm.domain.param.inventory.ExpiringLotsFilterParam
import app.devper.pharm.domain.param.reports.MovementsFilterParam
import app.devper.pharm.domain.param.inventory.WriteoffLotsParam
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.repository.FakeExpiringLotsRepository
import app.devper.pharm.domain.repository.FakeMovementsRepository
import app.devper.pharm.domain.testDispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private fun drug(id: String, name: String = "Drug $id") = Drug(
    id = id, name = name, genericName = null, type = null, strength = null,
    barcode = null, sellPrice = Money(10.0), costPrice = Money.Zero,
    stock = Quantity(2), minStock = Quantity(10),
    unit = "เม็ด", regNo = null,
)

class GetMovementsUseCaseTest {

    @Test
    fun forwards_filter_to_repository_and_returns_page() = runTest {
        val page = StockMovementsPage(items = emptyList(), total = 0)
        val repo = FakeMovementsRepository(page = page)
        val filter = MovementsFilterParam(
            from = LocalDate(2026, 5, 1), to = LocalDate(2026, 5, 31), limit = 50,
        )

        val result = GetMovementsUseCase(repo, testDispatchers()).invoke(filter)

        assertEquals(page, result.getOrThrow())
        assertEquals(filter, repo.lastFilter)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeMovementsRepository(throws = true)

        val result = GetMovementsUseCase(repo, testDispatchers()).invoke(MovementsFilterParam())

        assertTrue(result.isFailure)
    }
}

class GetExpiringLotsUseCaseTest {

    @Test
    fun forwards_filter_to_repository_and_returns_list() = runTest {
        val seed = listOf(
            ExpiringLot(
                id = "l1", drugId = "d1", drugName = "Drug A", lotNumber = "LOT-1",
                expiryDate = LocalDate(2026, 6, 30), remaining = 50, daysLeft = 30,
            ),
        )
        val repo = FakeExpiringLotsRepository(seed = seed)
        val filter = ExpiringLotsFilterParam(daysAhead = 60)

        val result = GetExpiringLotsUseCase(repo, testDispatchers()).invoke(filter)

        assertEquals(seed, result.getOrThrow())
        assertEquals(filter, repo.lastFilter)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeExpiringLotsRepository(listThrows = true)

        val result = GetExpiringLotsUseCase(repo, testDispatchers()).invoke(ExpiringLotsFilterParam())

        assertTrue(result.isFailure)
    }
}

class WriteoffLotsUseCaseTest {

    @Test
    fun forwards_param_and_returns_result() = runTest {
        val repo = FakeExpiringLotsRepository(
            writeoffResult = WriteoffResult(writtenOff = 2, failures = emptyList()),
        )
        val param = WriteoffLotsParam(lotIds = listOf("l1", "l2"))

        val result = WriteoffLotsUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(2, result.writtenOff)
        assertEquals(param, repo.lastWriteoff)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeExpiringLotsRepository(writeoffThrows = true)

        val result = WriteoffLotsUseCase(repo, testDispatchers()).invoke(
            WriteoffLotsParam(lotIds = listOf("l1")),
        )

        assertTrue(result.isFailure)
    }
}

class GetLowStockDrugsUseCaseTest {

    @Test
    fun returns_repository_low_stock_seed() = runTest {
        val seed = listOf(drug("d1", "Low A"), drug("d2", "Low B"))
        val repo = FakeDrugRepository(lowStockSeed = seed)

        val result = GetLowStockDrugsUseCase(repo, testDispatchers()).invoke()

        assertEquals(seed, result.getOrThrow())
    }

    @Test
    fun empty_seed_returns_empty_list() = runTest {
        val repo = FakeDrugRepository()

        val result = GetLowStockDrugsUseCase(repo, testDispatchers()).invoke()

        assertEquals(emptyList(), result.getOrThrow())
    }
}
