package app.devper.pharm.presentation.reports

import app.devper.pharm.domain.model.DrugProfit
import app.devper.pharm.domain.model.ProfitReport
import app.devper.pharm.domain.model.ProfitSummary
import kotlin.test.Test
import kotlin.test.assertEquals

class ProfitUiStateTest {

    private val rowA = DrugProfit(
        drugId = "a", drugName = "A",
        qtySold = 10, revenue = 1000.0, cost = 700.0, profit = 300.0, margin = 30.0,
    )
    private val rowB = DrugProfit(
        drugId = "b", drugName = "B",
        qtySold = 50, revenue = 500.0, cost = 300.0, profit = 200.0, margin = 40.0,
    )
    private val rowC = DrugProfit(
        drugId = "c", drugName = "C",
        qtySold = 5, revenue = 2000.0, cost = 1500.0, profit = 500.0, margin = 25.0,
    )

    private fun stateWith(sort: ProfitSort): ProfitUiState = ProfitUiState(
        sort = sort,
        report = ProfitReport(
            summary = ProfitSummary(0.0, 0.0, 0.0, 0.0, 0),
            byDrug = listOf(rowA, rowB, rowC),
        ),
    )

    @Test
    fun sort_by_profit_desc() {

        assertEquals(listOf("c", "a", "b"), stateWith(ProfitSort.Profit).sortedRows.map { it.drugId })
    }

    @Test
    fun sort_by_margin_desc() {

        assertEquals(listOf("b", "a", "c"), stateWith(ProfitSort.Margin).sortedRows.map { it.drugId })
    }

    @Test
    fun sort_by_qtySold_desc() {

        assertEquals(listOf("b", "a", "c"), stateWith(ProfitSort.QtySold).sortedRows.map { it.drugId })
    }

    @Test
    fun sort_by_revenue_desc() {

        assertEquals(listOf("c", "a", "b"), stateWith(ProfitSort.Revenue).sortedRows.map { it.drugId })
    }

    @Test
    fun sortedRows_returns_empty_when_report_is_null() {
        val state = ProfitUiState(report = null)
        assertEquals(emptyList(), state.sortedRows)
    }

    @Test
    fun summary_passes_through_from_report() {
        val summary = ProfitSummary(revenue = 100.0, cost = 50.0, profit = 50.0, margin = 50.0, bills = 7)
        val state = ProfitUiState(report = ProfitReport(summary, emptyList()))
        assertEquals(summary, state.summary)
    }
}
