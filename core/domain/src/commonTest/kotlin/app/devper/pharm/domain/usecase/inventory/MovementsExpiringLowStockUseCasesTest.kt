@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

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
