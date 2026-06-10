package app.devper.pharm.presentation.ky.i18n

import app.devper.pharm.common.NetworkException
import app.devper.pharm.presentation.ky.exception.KyUiStateError
import app.devper.pharm.ui.i18n.PharmStringsEn
import app.devper.pharm.ui.i18n.PharmStringsTh
import app.devper.pharm.ui.i18n.localizeCommon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class KyUiStateErrorLocalizeTest {

    @Test
    fun feature_error_maps_to_its_own_string_in_both_languages() {
        val error = KyUiStateError.DownloadPdfFailed()
        assertEquals(PharmStringsTh.kyDownloadPdfFailed, error.localizeKy(PharmStringsTh))
        assertEquals(PharmStringsEn.kyDownloadPdfFailed, error.localizeKy(PharmStringsEn))
        assertTrue(error.localizeKy(PharmStringsTh).isNotBlank())
        assertNotEquals(error.localizeKy(PharmStringsTh), error.localizeKy(PharmStringsEn))
    }

    @Test
    fun unmapped_exception_delegates_to_localizeCommon() {
        val transport = NetworkException()
        assertEquals(transport.localizeCommon(PharmStringsTh), transport.localizeKy(PharmStringsTh))
        assertEquals(PharmStringsEn.commonErrorNetwork, transport.localizeKy(PharmStringsEn))
    }
}
