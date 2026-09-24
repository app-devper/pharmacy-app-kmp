package app.devper.pharm.ui.components

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Recomposer
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class CompactPageRegistrationTest {
    @Test
    fun leaving_a_page_removes_its_topbar_actions() = runTest {
        val controller = CompactPageActionsController()
        val recomposer = Recomposer(coroutineContext)
        val page = Composition(EmptyApplier(), recomposer)
        try {
            page.setContent {
                CompositionLocalProvider(LocalCompactPageActionsController provides controller) {
                    CompactPageActions {}
                }
            }
            assertNotNull(controller.content)
            page.dispose()
            assertNull(controller.content)
        } finally {
            page.dispose()
            recomposer.close()
        }
    }

    @Test
    fun disposing_previous_page_does_not_clear_new_pages_header() = runTest {
        val controller = CompactPageHeaderController()
        val recomposer = Recomposer(coroutineContext)
        val previous = Composition(EmptyApplier(), recomposer)
        val next = Composition(EmptyApplier(), recomposer)
        try {
            previous.setContent {
                CompositionLocalProvider(LocalCompactPageHeaderController provides controller) {
                    CompactPageHeader("Previous", {}, null)
                }
            }
            next.setContent {
                CompositionLocalProvider(LocalCompactPageHeaderController provides controller) {
                    CompactPageHeader("Next", {}, null)
                }
            }
            val nextContent = assertNotNull(controller.content)
            previous.dispose()
            assertSame(nextContent, controller.content)
            assertEquals("Next", controller.content?.title)
            next.dispose()
            assertNull(controller.content)
        } finally {
            previous.dispose()
            next.dispose()
            recomposer.close()
        }
    }
}

private class EmptyApplier : AbstractApplier<Unit>(Unit) {
    override fun insertTopDown(index: Int, instance: Unit) {}
    override fun insertBottomUp(index: Int, instance: Unit) {}
    override fun remove(index: Int, count: Int) {}
    override fun move(from: Int, to: Int, count: Int) {}
    override fun onClear() {}
}
