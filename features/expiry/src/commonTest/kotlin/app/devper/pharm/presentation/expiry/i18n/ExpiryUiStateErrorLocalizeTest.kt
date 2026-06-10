package app.devper.pharm.presentation.expiry.i18n

import app.devper.pharm.common.NetworkException
import app.devper.pharm.presentation.expiry.exception.ExpiryUiStateError
import app.devper.pharm.ui.i18n.PharmStringsEn
import app.devper.pharm.ui.i18n.PharmStringsTh
import app.devper.pharm.ui.i18n.localizeCommon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ExpiryUiStateErrorLocalizeTest {

    @Test
    fun feature_error_maps_to_its_own_string_in_both_languages() {
        val error = ExpiryUiStateError.WriteoffFailed()
        assertEquals(PharmStringsTh.expiryWriteoffFailed, error.localizeExpiry(PharmStringsTh))
        assertEquals(PharmStringsEn.expiryWriteoffFailed, error.localizeExpiry(PharmStringsEn))
        assertTrue(error.localizeExpiry(PharmStringsTh).isNotBlank())
        assertNotEquals(error.localizeExpiry(PharmStringsTh), error.localizeExpiry(PharmStringsEn))
    }

    @Test
    fun unmapped_exception_delegates_to_localizeCommon() {
        val transport = NetworkException()
        assertEquals(transport.localizeCommon(PharmStringsTh), transport.localizeExpiry(PharmStringsTh))
        assertEquals(PharmStringsEn.commonErrorNetwork, transport.localizeExpiry(PharmStringsEn))
    }
}
