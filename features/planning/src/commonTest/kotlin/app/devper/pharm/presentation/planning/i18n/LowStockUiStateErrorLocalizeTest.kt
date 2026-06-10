package app.devper.pharm.presentation.planning.i18n

import app.devper.pharm.common.NetworkException
import app.devper.pharm.presentation.planning.exception.LowStockUiStateError
import app.devper.pharm.ui.i18n.PharmStringsEn
import app.devper.pharm.ui.i18n.PharmStringsTh
import app.devper.pharm.ui.i18n.localizeCommon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class LowStockUiStateErrorLocalizeTest {

    @Test
    fun feature_error_maps_to_its_own_string_in_both_languages() {
        val error = LowStockUiStateError.TrackStockFailed()
        assertEquals(PharmStringsTh.planningTrackStockFailed, error.localizePlanning(PharmStringsTh))
        assertEquals(PharmStringsEn.planningTrackStockFailed, error.localizePlanning(PharmStringsEn))
        assertTrue(error.localizePlanning(PharmStringsTh).isNotBlank())
        assertNotEquals(error.localizePlanning(PharmStringsTh), error.localizePlanning(PharmStringsEn))
    }

    @Test
    fun unmapped_exception_delegates_to_localizeCommon() {
        val transport = NetworkException()
        assertEquals(transport.localizeCommon(PharmStringsTh), transport.localizePlanning(PharmStringsTh))
        assertEquals(PharmStringsEn.commonErrorNetwork, transport.localizePlanning(PharmStringsEn))
    }
}
