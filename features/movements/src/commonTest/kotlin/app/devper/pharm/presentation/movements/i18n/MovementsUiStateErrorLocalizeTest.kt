package app.devper.pharm.presentation.movements.i18n

import app.devper.pharm.common.NetworkException
import app.devper.pharm.presentation.movements.exception.MovementsUiStateError
import app.devper.pharm.ui.i18n.PharmStringsEn
import app.devper.pharm.ui.i18n.PharmStringsTh
import app.devper.pharm.ui.i18n.localizeCommon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class MovementsUiStateErrorLocalizeTest {

    @Test
    fun feature_error_maps_to_its_own_string_in_both_languages() {
        val error = MovementsUiStateError.LoadHistoryFailed()
        assertEquals(PharmStringsTh.movementsLoadHistoryFailed, error.localizeMovements(PharmStringsTh))
        assertEquals(PharmStringsEn.movementsLoadHistoryFailed, error.localizeMovements(PharmStringsEn))
        assertTrue(error.localizeMovements(PharmStringsTh).isNotBlank())
        assertNotEquals(error.localizeMovements(PharmStringsTh), error.localizeMovements(PharmStringsEn))
    }

    @Test
    fun unmapped_exception_delegates_to_localizeCommon() {
        val transport = NetworkException()
        assertEquals(transport.localizeCommon(PharmStringsTh), transport.localizeMovements(PharmStringsTh))
        assertEquals(PharmStringsEn.commonErrorNetwork, transport.localizeMovements(PharmStringsEn))
    }
}
