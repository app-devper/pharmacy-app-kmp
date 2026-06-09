package app.devper.pharm.presentation.offlinesync

import app.devper.pharm.presentation.offlinesync.exception.OfflineSyncUiStateError
import app.devper.pharm.presentation.offlinesync.i18n.localize
import app.devper.pharm.presentation.offlinesync.message.OfflineSyncUiStateMessage
import app.devper.pharm.ui.i18n.PharmStringsEn
import app.devper.pharm.ui.i18n.PharmStringsTh
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class OfflineSyncLocalizeTest {

    @Test
    fun error_localizes_and_interpolates_per_locale() {
        val partial = OfflineSyncUiStateError.SyncPartialFailed(failed = 2, total = 5)
        assertTrue(partial.localize(PharmStringsTh).contains("2"))
        assertTrue(partial.localize(PharmStringsTh).contains("5"))
        assertTrue(partial.localize(PharmStringsEn).contains("2"))
        assertNotEquals(partial.localize(PharmStringsTh), partial.localize(PharmStringsEn))

        val retry = OfflineSyncUiStateError.RetryFailed(billId = "abc123")
        assertTrue(retry.localize(PharmStringsTh).contains("abc123"))
        assertTrue(retry.localize(PharmStringsEn).contains("abc123"))
    }

    @Test
    fun message_localizes_and_interpolates_per_locale() {
        val started = OfflineSyncUiStateMessage.SyncStarted(count = 7)
        assertTrue(started.localize(PharmStringsTh).contains("7"))
        assertTrue(started.localize(PharmStringsEn).contains("7"))
        assertNotEquals(started.localize(PharmStringsTh), started.localize(PharmStringsEn))

        val retry = OfflineSyncUiStateMessage.RetryStarted(billId = "deadbeef")
        assertTrue(retry.localize(PharmStringsTh).contains("deadbeef"))
        assertTrue(retry.localize(PharmStringsEn).contains("deadbeef"))
    }
}
