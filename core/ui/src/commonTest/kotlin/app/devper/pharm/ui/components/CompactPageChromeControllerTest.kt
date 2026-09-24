package app.devper.pharm.ui.components

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CompactPageChromeControllerTest {
    @Test
    fun header_keeps_priority_when_older_actions_reregister() {
        val controller = CompactPageChromeController()
        val actionsToken = Any()
        val headerToken = Any()

        controller.register(actionsToken, CompactPageChrome.Actions {})
        controller.register(headerToken, CompactPageChrome.Header("Edit", {}, null))
        controller.register(actionsToken, CompactPageChrome.Actions {})

        assertEquals("Edit", (controller.content as? CompactPageChrome.Header)?.title)
        controller.unregister(headerToken)
        assertEquals(true, controller.content is CompactPageChrome.Actions)
    }

    @Test
    fun older_header_cannot_replace_new_header_after_recomposition() {
        val controller = CompactPageChromeController()
        val oldToken = Any()
        val newToken = Any()

        controller.register(oldToken, CompactPageChrome.Header("Old", {}, null))
        controller.register(newToken, CompactPageChrome.Header("New", {}, null))
        controller.register(oldToken, CompactPageChrome.Header("Old updated", {}, null))

        assertEquals("New", (controller.content as? CompactPageChrome.Header)?.title)
        controller.unregister(oldToken)
        assertEquals("New", (controller.content as? CompactPageChrome.Header)?.title)
        controller.unregister(newToken)
        assertNull(controller.content)
    }
}
