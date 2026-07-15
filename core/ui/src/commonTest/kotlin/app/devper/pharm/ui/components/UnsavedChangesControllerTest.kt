package app.devper.pharm.ui.components

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UnsavedChangesControllerTest {
    @Test
    fun request_executes_immediately_when_form_is_clean() {
        val controller = UnsavedChangesController()
        var executed = false

        controller.request { executed = true }

        assertTrue(executed)
        assertFalse(controller.dialogOpen)
    }

    @Test
    fun request_waits_for_confirmation_when_form_is_dirty() {
        val controller = UnsavedChangesController()
        val token = Any()
        var executed = false
        controller.register(token, true)

        controller.request { executed = true }

        assertFalse(executed)
        assertTrue(controller.dialogOpen)
        controller.keepEditing()
        assertFalse(executed)
        assertFalse(controller.dialogOpen)
    }

    @Test
    fun discard_executes_pending_action_and_clears_dirty_state() {
        val controller = UnsavedChangesController()
        val token = Any()
        var executed = false
        controller.register(token, true)
        controller.request { executed = true }

        controller.discardChanges()

        assertTrue(executed)
        assertFalse(controller.hasUnsavedChanges)
        assertFalse(controller.dialogOpen)
    }

    @Test
    fun stale_registration_cannot_clear_current_form_state() {
        val controller = UnsavedChangesController()
        val staleToken = Any()
        val currentToken = Any()
        controller.register(staleToken, true)
        controller.register(currentToken, true)

        controller.unregister(staleToken)

        assertTrue(controller.hasUnsavedChanges)
    }
}
