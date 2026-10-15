@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.inventory.GetReorderSuggestionsUseCase

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.param.reports.DashboardRangeParam
import app.devper.pharm.domain.param.reports.EodReportParam
import app.devper.pharm.domain.param.inventory.ReorderSuggestionsParam
import app.devper.pharm.domain.param.reports.ReportRangeParam
import app.devper.pharm.domain.param.reports.TopOrSlowDrugsParam
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.repository.FakeReportsRepository
import app.devper.pharm.domain.testDispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private fun reorderSuggestion(drugId: String, drugName: String = "Drug $drugId") = ReorderSuggestion(
    drugId = drugId, drugName = drugName, unit = "เม็ด",
    currentStock = Quantity(2), minStock = Quantity(10),
    qtySold = Quantity(20), avgDailySale = 2.5, daysLeft = 1.0,
    suggestedQty = Quantity(30), costPrice = Money(1.0), sellPrice = Money(3.0),
)

class GetReorderSuggestionsUseCaseTest {

    @Test
    fun direct_param_returns_repository_seed() = runTest {
        val seed = listOf(reorderSuggestion("d1"), reorderSuggestion("d2"))
        val repo = FakeDrugRepository(reorderSeed = seed)
        val param = ReorderSuggestionsParam(days = 30, lookahead = 14)

        val result = GetReorderSuggestionsUseCase(repo, testDispatchers()).invoke(param)

        assertEquals(seed, result.getOrThrow())
        assertEquals(param, repo.lastReorderParam)
    }

    @Test
    fun convenience_invoke_uses_default_param() = runTest {
        val seed = listOf(reorderSuggestion("d1"))
        val repo = FakeDrugRepository(reorderSeed = seed)

        val result = GetReorderSuggestionsUseCase(repo, testDispatchers()).invoke()

        assertEquals(seed, result.getOrThrow())
        assertEquals(ReorderSuggestionsParam(), repo.lastReorderParam)
    }
}
