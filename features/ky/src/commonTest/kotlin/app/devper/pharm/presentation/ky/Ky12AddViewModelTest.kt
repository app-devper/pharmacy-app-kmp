package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.repository.FakeKyRepository
import app.devper.pharm.domain.usecase.ky.AddKy12UseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class Ky12AddViewModelTest {

    private fun fill(vm: Ky12AddViewModel) {
        vm.onDate("2026-06-01")
        vm.onDrugName("Morphine 10mg")
        vm.onUnit("amp")
        vm.onQty("10")
    }

    @Test
    fun total_value_setter_strips_letters_and_extra_dots() = runVmTest { d ->
        val vm = Ky12AddViewModel(AddKy12UseCase(FakeKyRepository(), d))
        vm.onTotalValue("1a2.5b6.7")
        assertEquals("12.567", vm.state.value.draft.totalValue)
    }

    @Test
    fun submit_valid_draft_saves_and_records_form() = runVmTest { d ->
        val repo = FakeKyRepository()
        val vm = Ky12AddViewModel(AddKy12UseCase(repo, d))
        fill(vm)
        vm.submit()
        advanceUntilIdle()
        assertTrue(vm.state.value.saved)
        assertEquals(1, repo.ky12Submissions.size)
        assertEquals("Morphine 10mg", repo.ky12Submissions.first().drugName)
    }

    @Test
    fun submit_blank_draft_is_noop() = runVmTest { d ->
        val repo = FakeKyRepository()
        val vm = Ky12AddViewModel(AddKy12UseCase(repo, d))
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saved)
        assertTrue(repo.ky12Submissions.isEmpty())
    }
}
