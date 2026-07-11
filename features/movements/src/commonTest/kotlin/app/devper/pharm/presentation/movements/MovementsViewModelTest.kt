package app.devper.pharm.presentation.movements

import app.devper.pharm.domain.model.MovementType
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.domain.model.StockMovementsPage
import app.devper.pharm.domain.repository.FakeExportRepository
import app.devper.pharm.domain.repository.FakeMovementsRepository
import app.devper.pharm.domain.usecase.reports.ExportMovementsCsvUseCase
import app.devper.pharm.domain.usecase.reports.GetMovementsUseCase
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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
            app.devper.pharm.domain.observer.testTimeZoneProvider(),
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
            app.devper.pharm.domain.observer.testTimeZoneProvider(),
        )
        advanceUntilIdle()
        model.onExportExcel(listOf("h1", "h2"))
        advanceUntilIdle()
        assertIs<CommonUiStateMessage.ExportEmpty>(model.state.value.messageState)
        assertEquals(null, export.lastFilename)
    }

    @Test
    fun export_with_items_calls_save_and_sets_done_message() = runVmTest { d ->
        val export = FakeExportRepository(result = "/storage/movements.csv")
        val model = MovementsViewModel(
            GetMovementsUseCase(FakeMovementsRepository(StockMovementsPage(listOf(movement("a")), 1)), d),
            ExportMovementsCsvUseCase(export, d),
            app.devper.pharm.domain.observer.testTimeZoneProvider(),
        )
        advanceUntilIdle()
        model.onExportExcel(listOf("h1", "h2"))
        advanceUntilIdle()
        assertNotNull(export.lastFilename)
        assertIs<CommonUiStateMessage.ExportDone>(model.state.value.messageState)
    }

    @Test
    fun dismiss_message_clears_message_state() = runVmTest { d ->
        val export = FakeExportRepository()
        val model = MovementsViewModel(
            GetMovementsUseCase(FakeMovementsRepository(StockMovementsPage(emptyList(), 0)), d),
            ExportMovementsCsvUseCase(export, d),
            app.devper.pharm.domain.observer.testTimeZoneProvider(),
        )
        advanceUntilIdle()
        model.onExportExcel(listOf("h1", "h2"))
        advanceUntilIdle()
        assertNotNull(model.state.value.messageState)
        model.dismissMessage()
        assertNull(model.state.value.messageState)
    }

    @Test
    fun toggle_type_removes_then_readds_type_id() = runVmTest { d ->
        val model = vm(d, StockMovementsPage(emptyList(), 0))
        advanceUntilIdle()
        val initialIds = model.state.value.activeTypeIds
        val firstId = initialIds.first()
        model.onToggleType(firstId)
        assertFalse(firstId in model.state.value.activeTypeIds)
        model.onToggleType(firstId)
        assert(firstId in model.state.value.activeTypeIds)
    }

    @Test
    fun next_page_increments_and_prev_page_decrements() = runVmTest { d ->
        val items = (1..21).map { movement("x$it") }
        val model = vm(d, StockMovementsPage(items, total = 21))
        advanceUntilIdle()
        model.onNextPage()
        assertEquals(2, model.state.value.page)
        model.onPrevPage()
        assertEquals(1, model.state.value.page)
    }

    @Test
    fun prev_page_does_not_go_below_one() = runVmTest { d ->
        val model = vm(d, StockMovementsPage(emptyList(), 0))
        advanceUntilIdle()
        model.onPrevPage()
        assertEquals(1, model.state.value.page)
    }
}
