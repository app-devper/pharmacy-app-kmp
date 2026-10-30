@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.ui.common

import app.devper.pharm.common.AppException
import app.devper.pharm.common.NetworkException
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.common.error.CommonUiStateError
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

private data class Record(val id: String, val name: String, val locked: Boolean = false)

private data class EditState(
    val form: String = "",
    val baselineForm: String = "",
    val locked: Boolean = false,
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    val errorState: AppException? = null,
) : EditFormUiState<EditState, String> {
    override val canSubmit: Boolean get() = !loading && form.isNotBlank()
    override val hasUnsavedChanges: Boolean get() = form != baselineForm
    override val domainError: AppException? get() = errorState
    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override fun withDomainError(error: AppException?) = copy(errorState = error)
    override fun withLoading(loading: Boolean) = copy(loading = loading)
    override fun withHydrated(form: String) = copy(form = form, baselineForm = form)
}

private class EditViewModel : BaseEditFormViewModel<EditState, String>(EditState()) {
    fun open(fetch: suspend () -> Record?) = hydrate(
        fetch = fetch,
        notFound = { NotFoundException("missing") },
        failure = { e -> CommonUiStateError.LoadFailed(e) },
        toForm = { it.name },
        extra = { r -> copy(locked = r.locked) },
    )

    fun type(v: String) = setState { copy(form = v) }

    override suspend fun persist(): Result<Unit> = Result.success(Unit)
}

class BaseEditFormViewModelTest {

    @Test
    fun a_hydrated_form_starts_with_no_unsaved_changes() = runVmTest { _ ->
        val vm = EditViewModel()
        vm.open { Record("1", "Paracetamol", locked = true) }
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals("Paracetamol", s.form)
        assertFalse(s.hasUnsavedChanges)
        assertTrue(s.locked)
        assertFalse(s.loading)
        vm.type("Paracetamol 500")
        assertTrue(vm.state.value.hasUnsavedChanges)
    }

    @Test
    fun a_missing_record_is_not_found() = runVmTest { _ ->
        val vm = EditViewModel()
        vm.open { null }
        advanceUntilIdle()
        assertIs<NotFoundException>(vm.state.value.errorState)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun a_failed_load_is_typed() = runVmTest { _ ->
        val vm = EditViewModel()
        vm.open { throw NetworkException() }
        advanceUntilIdle()
        assertIs<CommonUiStateError.LoadFailed>(vm.state.value.errorState)
    }

    @Test
    fun a_slow_older_load_never_overwrites_a_newer_one() = runVmTest { _ ->
        val vm = EditViewModel()
        val slow = CompletableDeferred<Record?>()
        vm.open { slow.await() }
        vm.open { Record("2", "Amoxicillin") }
        advanceUntilIdle()
        slow.complete(Record("1", "Paracetamol"))
        advanceUntilIdle()
        assertEquals("Amoxicillin", vm.state.value.form)
    }
}
