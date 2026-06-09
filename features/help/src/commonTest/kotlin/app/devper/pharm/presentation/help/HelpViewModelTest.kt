package app.devper.pharm.presentation.help

import app.devper.pharm.presentation.help.exception.HelpException
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HelpViewModelTest {

    @Test
    fun init_loads_markdown_into_state() = runVmTest { _ ->
        val loader = FakeMarkdownLoader(content = "# คู่มือผู้ใช้\n\n...")
        val vm = HelpViewModel(loader)
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        assertEquals("# คู่มือผู้ใช้\n\n...", vm.state.value.markdown)
        assertNull(vm.state.value.errorTyped)
    }

    @Test
    fun load_failure_sets_typed_error_and_clears_loading() = runVmTest { _ ->
        val loader = FakeMarkdownLoader(failureMessage = "ไฟล์หาย")
        val vm = HelpViewModel(loader)
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        assertTrue(vm.state.value.errorTyped is HelpException.Markdown.LoadFailed)
    }

    @Test
    fun dismiss_error_clears_typed_error() = runVmTest { _ ->
        val loader = FakeMarkdownLoader(failureMessage = "ไฟล์หาย")
        val vm = HelpViewModel(loader)
        advanceUntilIdle()
        vm.dismissError()
        assertNull(vm.state.value.errorTyped)
    }

    @Test
    fun reload_reads_fresh_markdown() = runVmTest { _ ->
        val loader = FakeMarkdownLoader(content = "v1")
        val vm = HelpViewModel(loader)
        advanceUntilIdle()
        assertEquals("v1", vm.state.value.markdown)
        loader.content = "v2"
        vm.reload()
        advanceUntilIdle()
        assertEquals("v2", vm.state.value.markdown)
    }
}

private class FakeMarkdownLoader(
    var content: String = "",
    private val failureMessage: String? = null,
) : MarkdownLoader {
    override suspend fun loadUserGuide(): String {
        failureMessage?.let { throw RuntimeException(it) }
        return content
    }
}
