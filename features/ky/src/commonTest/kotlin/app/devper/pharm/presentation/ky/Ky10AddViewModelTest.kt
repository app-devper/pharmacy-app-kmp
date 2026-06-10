package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.repository.FakeKyRepository
import app.devper.pharm.domain.usecase.ky.AddKy10UseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class Ky10AddViewModelTest {

    private fun fill(vm: Ky10AddViewModel) {
        vm.onDate("2026-06-01")
        vm.onDrugName("Diazepam 5mg")
        vm.onUnit("เม็ด")
        vm.onQty("30")
    }

    @Test
    fun field_setters_update_draft_with_digit_only_qty() = runVmTest { d ->
        val vm = Ky10AddViewModel(AddKy10UseCase(FakeKyRepository(), d))
        vm.onDrugName("Morphine")
        vm.onQty("12a3")
        vm.onBalance("10x")
        assertEquals("Morphine", vm.state.value.draft.drugName)
        assertEquals("123", vm.state.value.draft.qty)
        assertEquals("10", vm.state.value.draft.balance)
    }

    @Test
    fun submit_valid_draft_saves_and_records_form() = runVmTest { d ->
        val repo = FakeKyRepository()
        val vm = Ky10AddViewModel(AddKy10UseCase(repo, d))
        fill(vm)
        vm.submit()
        advanceUntilIdle()
        assertTrue(vm.state.value.saved)
        assertEquals(1, repo.ky10Submissions.size)
        assertEquals("Diazepam 5mg", repo.ky10Submissions.first().drugName)
    }

    @Test
    fun submit_blank_draft_is_noop() = runVmTest { d ->
        val repo = FakeKyRepository()
        val vm = Ky10AddViewModel(AddKy10UseCase(repo, d))
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saved)
        assertTrue(repo.ky10Submissions.isEmpty())
    }
}
