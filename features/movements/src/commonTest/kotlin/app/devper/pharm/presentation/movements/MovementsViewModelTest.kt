package app.devper.pharm.presentation.movements

import app.devper.pharm.domain.model.MovementType
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.domain.model.StockMovementsPage
import app.devper.pharm.domain.repository.FakeExportRepository
import app.devper.pharm.domain.repository.FakeMovementsRepository
import app.devper.pharm.domain.usecase.ExportMovementsCsvUseCase
import app.devper.pharm.domain.usecase.GetMovementsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class MovementsViewModelTest {

    private fun movement(id: String) = StockMovement(
        id = id, type = MovementType.Sale, drugId = "drug-$id", drugName = "Drug $id",
        delta = -1, reference = "ref", note = "", at = "2026-05-01T10:00:00+07:00",
    )

    private fun vm(d: app.devper.pharm.common.AppDispatchers, page: StockMovementsPage) =
        MovementsViewModel(
            GetMovementsUseCase(FakeMovementsRepository(page), d),
            ExportMovementsCsvUseCase(FakeExportRepository(), d),
        )

    @Test
    fun init_loads_movements() = runVmTest { d ->
        val page = StockMovementsPage(items = listOf(movement("a"), movement("b")), total = 2)
        val model = vm(d, page)
        advanceUntilIdle()
        assertEquals(2, model.state.value.items.size)
        assertEquals(2, model.state.value.total)
        assertFalse(model.state.value.loading)
    }

    @Test
    fun search_change_resets_page_to_one() = runVmTest { d ->
        val model = vm(d, StockMovementsPage(items = emptyList(), total = 0))
        advanceUntilIdle()
        model.onSearchChange("para")
        assertEquals("para", model.state.value.drugName)
        assertEquals(1, model.state.value.page)
    }

    @Test
    fun export_with_empty_items_sets_message_without_calling_export() = runVmTest { d ->
        val export = FakeExportRepository()
        val model = MovementsViewModel(
            GetMovementsUseCase(FakeMovementsRepository(StockMovementsPage(emptyList(), 0)), d),
            ExportMovementsCsvUseCase(export, d),
        )
        advanceUntilIdle()
        model.onExportExcel()
        advanceUntilIdle()
        assertNotNull(model.state.value.message)
        assertEquals(null, export.lastFilename)
    }
}
