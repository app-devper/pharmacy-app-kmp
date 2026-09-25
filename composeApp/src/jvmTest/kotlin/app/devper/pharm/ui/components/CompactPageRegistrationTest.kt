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
    fun header_replaces_actions_and_stale_action_disposal_keeps_header() = runTest {
        val controller = CompactPageChromeController()
        val recomposer = Recomposer(coroutineContext)
        val listPage = Composition(EmptyApplier(), recomposer)
        val detailPage = Composition(EmptyApplier(), recomposer)
        try {
            listPage.setContent {
                CompositionLocalProvider(LocalCompactPageChromeController provides controller) {
                    RegisterCompactPageChrome(CompactPageChrome.Actions {})
                }
            }
            assertNotNull(controller.content as? CompactPageChrome.Actions)

            detailPage.setContent {
                CompositionLocalProvider(LocalCompactPageChromeController provides controller) {
                    RegisterCompactPageChrome(CompactPageChrome.Header("Detail", {}, null))
                }
            }
            listPage.dispose()
            assertEquals("Detail", (controller.content as? CompactPageChrome.Header)?.title)
            detailPage.dispose()
            assertNull(controller.content)
        } finally {
            listPage.dispose()
            detailPage.dispose()
            recomposer.close()
        }
    }

    @Test
    fun leaving_a_page_removes_its_topbar_actions() = runTest {
        val controller = CompactPageChromeController()
        val recomposer = Recomposer(coroutineContext)
        val page = Composition(EmptyApplier(), recomposer)
        try {
            page.setContent {
                CompositionLocalProvider(LocalCompactPageChromeController provides controller) {
                    RegisterCompactPageChrome(CompactPageChrome.Actions {})
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
        val controller = CompactPageChromeController()
        val recomposer = Recomposer(coroutineContext)
        val previous = Composition(EmptyApplier(), recomposer)
        val next = Composition(EmptyApplier(), recomposer)
        try {
            previous.setContent {
                CompositionLocalProvider(LocalCompactPageChromeController provides controller) {
                    RegisterCompactPageChrome(CompactPageChrome.Header("Previous", {}, null))
                }
            }
            next.setContent {
                CompositionLocalProvider(LocalCompactPageChromeController provides controller) {
                    RegisterCompactPageChrome(CompactPageChrome.Header("Next", {}, null))
                }
            }
            val nextContent = assertNotNull(controller.content)
            previous.dispose()
            assertSame(nextContent, controller.content)
            assertEquals("Next", (controller.content as? CompactPageChrome.Header)?.title)
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
