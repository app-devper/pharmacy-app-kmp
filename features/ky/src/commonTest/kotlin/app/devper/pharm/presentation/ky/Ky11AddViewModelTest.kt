package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.repository.FakeKyRepository
import app.devper.pharm.domain.usecase.AddKy11UseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class Ky11AddViewModelTest {

    private fun fill(vm: Ky11AddViewModel) {
        vm.onDate("2026-06-01")
        vm.onDrugName("Pseudoephedrine 60mg")
        vm.onUnit("เม็ด")
        vm.onQty("20")
    }

    @Test
    fun field_setters_update_draft_with_digit_only_qty() = runVmTest { d ->
        val vm = Ky11AddViewModel(AddKy11UseCase(FakeKyRepository(), d))
        vm.onDrugName("Codeine")
        vm.onQty("4abc5")
        assertEquals("Codeine", vm.state.value.draft.drugName)
        assertEquals("45", vm.state.value.draft.qty)
    }

    @Test
    fun submit_valid_draft_saves_and_records_form() = runVmTest { d ->
        val repo = FakeKyRepository()
        val vm = Ky11AddViewModel(AddKy11UseCase(repo, d))
        fill(vm)
        vm.submit()
        advanceUntilIdle()
        assertTrue(vm.state.value.saved)
        assertEquals(1, repo.ky11Submissions.size)
        assertEquals("Pseudoephedrine 60mg", repo.ky11Submissions.first().drugName)
    }

    @Test
    fun submit_blank_draft_is_noop() = runVmTest { d ->
        val repo = FakeKyRepository()
        val vm = Ky11AddViewModel(AddKy11UseCase(repo, d))
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saved)
        assertTrue(repo.ky11Submissions.isEmpty())
    }
}
