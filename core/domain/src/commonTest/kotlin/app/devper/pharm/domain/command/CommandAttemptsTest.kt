package app.devper.pharm.domain.command

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CommandAttemptsTest {

    private fun attempts(): CommandAttempts {
        var n = 0
        return CommandAttempts { "id-${++n}" }
    }

    @Test
    fun the_same_request_keeps_its_id_until_it_is_settled() {
        val a = attempts()
        assertEquals("id-1", a.idFor(CommandKind.Sale, "cart"))
        assertEquals("id-1", a.idFor(CommandKind.Sale, "cart"))
        a.settled(CommandKind.Sale)
        assertEquals("id-2", a.idFor(CommandKind.Sale, "cart"))
    }

    @Test
    fun a_changed_request_gets_a_new_id() {
        val a = attempts()
        val first = a.idFor(CommandKind.Sale, "cart")
        assertNotEquals(first, a.idFor(CommandKind.Sale, "other cart"))
    }

    @Test
    fun sales_and_returns_keep_separate_attempts() {
        val a = attempts()
        val sale = a.idFor(CommandKind.Sale, "x")
        val ret = a.idFor(CommandKind.Return, "x")
        assertNotEquals(sale, ret)
        assertEquals(sale, a.idFor(CommandKind.Sale, "x"))
    }
}
