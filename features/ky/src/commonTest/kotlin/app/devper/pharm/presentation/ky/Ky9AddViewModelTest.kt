package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.repository.FakeKyRepository
import app.devper.pharm.domain.usecase.ky.AddKy9UseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class Ky9AddViewModelTest {

    private fun fill(vm: Ky9AddViewModel) {
        vm.onDate("2026-05-01")
        vm.onDrugName("Paracetamol")
        vm.onUnit("เม็ด")
        vm.onQty("10")
        vm.onPricePerUnit("2.50")
    }

    @Test
    fun field_setters_update_draft() = runVmTest { d ->
        val vm = Ky9AddViewModel(AddKy9UseCase(FakeKyRepository(), d))
        vm.onDrugName("Amoxicillin")
        vm.onQty("12a3")
        assertEquals("Amoxicillin", vm.state.value.draft.drugName)
        assertEquals("123", vm.state.value.draft.qty)
    }

    @Test
    fun submit_valid_draft_saves_and_records_param() = runVmTest { d ->
        val repo = FakeKyRepository()
        val vm = Ky9AddViewModel(AddKy9UseCase(repo, d))
        fill(vm)
        vm.submit()
        advanceUntilIdle()
        assertTrue(vm.state.value.saved)
        assertEquals(1, repo.ky9Adds.size)
        assertEquals("Paracetamol", repo.ky9Adds.first().drugName)
    }

    @Test
    fun submit_blank_draft_is_noop() = runVmTest { d ->
        val repo = FakeKyRepository()
        val vm = Ky9AddViewModel(AddKy9UseCase(repo, d))
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saved)
        assertTrue(repo.ky9Adds.isEmpty())
    }
}
